/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coral;

import static io.opentelemetry.instrumentation.api.internal.AttributesExtractorUtil.internalSet;

import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.incubator.semconv.code.CodeAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.internal.SpanKey;
import io.opentelemetry.instrumentation.api.internal.SpanKeyProvider;
import io.opentelemetry.semconv.SemanticAttributes;
import javax.annotation.Nullable;

/**
 * Extractor of <a
 * href="https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/attributes.md#source-code-attributes">source
 * code attributes</a>.
 */
public final class CoralServerAttributesExtractor<REQUEST, RESPONSE>
    implements AttributesExtractor<REQUEST, RESPONSE>, SpanKeyProvider {

  /** Creates the code attributes extractor. */
  public static <REQUEST, RESPONSE> AttributesExtractor<REQUEST, RESPONSE> create(
      AttributesGetter<REQUEST> getter) {
    return new CoralServerAttributesExtractor<>(getter);
  }

  private final AttributesGetter<REQUEST> getter;

  private CoralServerAttributesExtractor(AttributesGetter<REQUEST> getter) {
    this.getter = getter;
  }

  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, REQUEST request) {
    Class<?> cls = getter.getCodeClass(request);
    if (cls != null) {
      internalSet(attributes, SemanticAttributes.CODE_NAMESPACE, cls.getName());
    }
    internalSet(attributes, SemanticAttributes.CODE_FUNCTION, getter.getMethodName(request));
    internalSet(attributes, SemanticAttributes.SERVER_ADDRESS,
        getter.getServerAddress(request)); // http host
    internalSet(attributes, SemanticAttributes.URL_PATH, getter.getUrlPath(request)); // http target
    internalSet(attributes, SemanticAttributes.HTTP_URL,
        getter.getServerAddress(request)); // http target
    internalSet(attributes, SemanticAttributes.CLIENT_ADDRESS,
        getter.getClientAddress(request)); // HTTP_CLIENT_IP
    internalSet(attributes, SemanticAttributes.HTTP_METHOD,
        getter.getHttpMethod(request)); // HTTP_VERB
    internalSet(attributes, SemanticAttributes.HTTP_USER_AGENT, getter.getUserAgent(request));
    internalSet(attributes, SemanticAttributes.HTTP_SCHEME, getter.getHttpSchema(request));
    internalSet(attributes, SemanticAttributes.NET_PEER_IP, getter.getNetPeerIp(request));
    internalSet(attributes, SemanticAttributes.RPC_SERVICE, getter.getServiceName(request));
    internalSet(attributes, SemanticAttributes.RPC_METHOD, getter.getMethodName(request));
  }

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      REQUEST request,
      @Nullable RESPONSE response,
      @Nullable Throwable error) {
    Integer statusCode = ((Job) response).getReply().getAttribute(HttpConstant.HTTP_STATUS_CODE);
    if (statusCode != null) {
      internalSet(attributes, SemanticAttributes.HTTP_STATUS_CODE, (long) statusCode);
    }
  }

  @Nullable
  @Override
  public SpanKey internalGetSpanKey() {
    return SpanKey.KIND_SERVER;
  }

}
