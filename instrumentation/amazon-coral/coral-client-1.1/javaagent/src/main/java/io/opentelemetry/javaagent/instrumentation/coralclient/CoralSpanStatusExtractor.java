/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coralclient;

import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.SpanStatusExtractor;
import io.opentelemetry.semconv.SemanticAttributes;
import javax.annotation.Nullable;

/**
 * Extractor of <a
 * href="https://github.com/open-telemetry/semantic-conventions/blob/main/docs/general/attributes.md#source-code-attributes">source
 * code attributes</a>.
 */
public final class CoralSpanStatusExtractor<REQUEST, RESPONSE>
    implements SpanStatusExtractor<REQUEST, RESPONSE> {
  public static <REQUEST, RESPONSE> SpanStatusExtractor<REQUEST, RESPONSE> create(
      AttributesGetter<REQUEST> getter) {
    return new CoralSpanStatusExtractor<>(getter);
  }

  private final AttributesGetter<REQUEST> getter;

  private CoralSpanStatusExtractor(AttributesGetter<REQUEST> getter) {
    this.getter = getter;
  }

  @Override
  public void extract(SpanStatusBuilder spanStatusBuilder, REQUEST request,
      @Nullable RESPONSE response, @Nullable Throwable error) {
    if (response != null) {
      Integer statusCode = ((Job) response).getReply().getAttribute(HttpConstant.HTTP_STATUS_CODE);
      if (statusCode != null) {
        if (isError(statusCode)) {
          spanStatusBuilder.setStatus(StatusCode.ERROR);
          return;
        }
      }
    }
    SpanStatusExtractor.getDefault().extract(spanStatusBuilder, request, response, error);
  }

  boolean isError(int responseStatusCode) {
    return responseStatusCode >= 500
        ||
        // invalid status code, does not exists
        responseStatusCode < 100;
  }
}
