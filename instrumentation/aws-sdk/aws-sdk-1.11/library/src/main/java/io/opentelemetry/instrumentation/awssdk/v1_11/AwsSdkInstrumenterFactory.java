/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import com.amazonaws.Request;
import com.amazonaws.Response;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpClientAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessageOperation;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingSpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.rpc.RpcClientAttributesExtractor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;

final class AwsSdkInstrumenterFactory {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.aws-sdk-1.11";

  private static final AttributesExtractor<Request<?>, Response<?>> httpAttributesExtractor =
      HttpClientAttributesExtractor.create(new AwsSdkHttpAttributesGetter());
  private static final AttributesExtractor<Request<?>, Response<?>> rpcAttributesExtractor =
      RpcClientAttributesExtractor.create(AwsSdkRpcAttributesGetter.INSTANCE);
  private static final AwsSdkExperimentalAttributesExtractor experimentalAttributesExtractor =
      new AwsSdkExperimentalAttributesExtractor();
  private static final BedrockAttributesExtractor bedrockAttributesExtractor =
      new BedrockAttributesExtractor();
  private static final SnsAttributesExtractor snsAttributesExtractor = new SnsAttributesExtractor();

  private static final List<AttributesExtractor<Request<?>, Response<?>>>
      defaultAttributesExtractors =
      Arrays.asList(httpAttributesExtractor, rpcAttributesExtractor, snsAttributesExtractor);
  private static final List<AttributesExtractor<Request<?>, Response<?>>>
      extendedAttributesExtractors =
          Arrays.asList(
              httpAttributesExtractor,
              rpcAttributesExtractor,
              experimentalAttributesExtractor,
              snsAttributesExtractor,
              bedrockAttributesExtractor);

  private static final AwsSdkSpanNameExtractor spanName = new AwsSdkSpanNameExtractor();

  private final OpenTelemetry openTelemetry;
  private final List<String> capturedHeaders;
  private final boolean captureExperimentalSpanAttributes;
  private final boolean messagingReceiveInstrumentationEnabled;

  AwsSdkInstrumenterFactory(
      OpenTelemetry openTelemetry,
      List<String> capturedHeaders,
      boolean captureExperimentalSpanAttributes,
      boolean messagingReceiveInstrumentationEnabled) {
    this.openTelemetry = openTelemetry;
    this.capturedHeaders = capturedHeaders;
    this.captureExperimentalSpanAttributes = captureExperimentalSpanAttributes;
    this.messagingReceiveInstrumentationEnabled = messagingReceiveInstrumentationEnabled;
  }

  Instrumenter<Request<?>, Response<?>> requestInstrumenter() {
    return createInstrumenter(
        openTelemetry,
        spanName,
        SpanKindExtractor.alwaysClient(),
        attributesExtractors(),
        emptyList(),
        true);
  }

  private List<AttributesExtractor<Request<?>, Response<?>>> attributesExtractors() {
    return captureExperimentalSpanAttributes
        ? extendedAttributesExtractors
        : defaultAttributesExtractors;
  }

  private <REQUEST, RESPONSE> AttributesExtractor<REQUEST, RESPONSE> messagingAttributesExtractor(
      MessagingAttributesGetter<REQUEST, RESPONSE> getter, MessageOperation operation) {
    return MessagingAttributesExtractor.builder(getter, operation)
        .setCapturedHeaders(capturedHeaders)
        .build();
  }

  Instrumenter<SqsReceiveRequest, Response<?>> consumerReceiveInstrumenter() {
    MessageOperation operation = MessageOperation.RECEIVE;
    SqsReceiveRequestAttributesGetter getter = SqsReceiveRequestAttributesGetter.INSTANCE;
    AttributesExtractor<SqsReceiveRequest, Response<?>> messagingAttributeExtractor =
        messagingAttributesExtractor(getter, operation);

    return createInstrumenter(
        openTelemetry,
        MessagingSpanNameExtractor.create(getter, operation),
        SpanKindExtractor.alwaysConsumer(),
        toSqsRequestExtractors(attributesExtractors(), Function.identity()),
        singletonList(messagingAttributeExtractor),
        messagingReceiveInstrumentationEnabled);
  }

  Instrumenter<SqsProcessRequest, Void> consumerProcessInstrumenter() {
    MessageOperation operation = MessageOperation.PROCESS;
    SqsProcessRequestAttributesGetter getter = SqsProcessRequestAttributesGetter.INSTANCE;
    AttributesExtractor<SqsProcessRequest, Void> messagingAttributeExtractor =
        messagingAttributesExtractor(getter, operation);

    InstrumenterBuilder<SqsProcessRequest, Void> builder =
        Instrumenter.<SqsProcessRequest, Void>builder(
                openTelemetry,
                INSTRUMENTATION_NAME,
                MessagingSpanNameExtractor.create(getter, operation))
            .addAttributesExtractors(toSqsRequestExtractors(attributesExtractors(), unused -> null))
            .addAttributesExtractor(messagingAttributeExtractor);

    if (messagingReceiveInstrumentationEnabled) {
      builder.addSpanLinksExtractor(
          (spanLinks, parentContext, request) -> {
            Context extracted =
                SqsParentContext.ofSystemAttributes(request.getMessage().getAttributes());
            spanLinks.addLink(Span.fromContext(extracted).getSpanContext());
          });
    }
    return builder.buildInstrumenter(SpanKindExtractor.alwaysConsumer());
  }

  private static <RESPONSE>
      List<AttributesExtractor<AbstractSqsRequest, RESPONSE>> toSqsRequestExtractors(
          List<AttributesExtractor<Request<?>, Response<?>>> extractors,
          Function<RESPONSE, Response<?>> responseConverter) {
    List<AttributesExtractor<AbstractSqsRequest, RESPONSE>> result = new ArrayList<>();
    for (AttributesExtractor<Request<?>, Response<?>> extractor : extractors) {
      result.add(
          new AttributesExtractor<AbstractSqsRequest, RESPONSE>() {
            @Override
            public void onStart(
                AttributesBuilder attributes,
                Context parentContext,
                AbstractSqsRequest sqsRequest) {
              extractor.onStart(attributes, parentContext, sqsRequest.getRequest());
            }

            @Override
            public void onEnd(
                AttributesBuilder attributes,
                Context context,
                AbstractSqsRequest sqsRequest,
                @Nullable RESPONSE response,
                @Nullable Throwable error) {
              extractor.onEnd(
                  attributes,
                  context,
                  sqsRequest.getRequest(),
                  responseConverter.apply(response),
                  error);
            }
          });
    }
    return result;
  }

  Instrumenter<Request<?>, Response<?>> producerInstrumenter() {
    MessageOperation operation = MessageOperation.PUBLISH;
    SqsAttributesGetter getter = SqsAttributesGetter.INSTANCE;
    AttributesExtractor<Request<?>, Response<?>> messagingAttributeExtractor =
        messagingAttributesExtractor(getter, operation);

    return createInstrumenter(
        openTelemetry,
        MessagingSpanNameExtractor.create(getter, operation),
        SpanKindExtractor.alwaysProducer(),
        attributesExtractors(),
        singletonList(messagingAttributeExtractor),
        true);
  }

  private static <REQUEST, RESPONSE> Instrumenter<REQUEST, RESPONSE> createInstrumenter(
      OpenTelemetry openTelemetry,
      SpanNameExtractor<REQUEST> spanNameExtractor,
      SpanKindExtractor<REQUEST> spanKindExtractor,
      List<? extends AttributesExtractor<? super REQUEST, ? super RESPONSE>> attributeExtractors,
      List<AttributesExtractor<REQUEST, RESPONSE>> additionalAttributeExtractors,
      boolean enabled) {
    return Instrumenter.<REQUEST, RESPONSE>builder(
            openTelemetry, INSTRUMENTATION_NAME, spanNameExtractor)
        .addAttributesExtractors(attributeExtractors)
        .addAttributesExtractors(additionalAttributeExtractors)
        .setEnabled(enabled)
        .buildInstrumenter(spanKindExtractor);
  }
}
