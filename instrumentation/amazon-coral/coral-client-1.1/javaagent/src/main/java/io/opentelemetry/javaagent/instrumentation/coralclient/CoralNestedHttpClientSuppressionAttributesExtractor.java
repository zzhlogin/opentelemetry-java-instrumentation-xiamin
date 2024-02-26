/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coralclient;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.internal.SpanKey;
import io.opentelemetry.instrumentation.api.internal.SpanKeyProvider;
import io.opentelemetry.instrumentation.api.semconv.http.HttpClientAttributesExtractor;

import javax.annotation.Nullable;


/**
 * An attribute extractor that reports implementing HTTP client semantic conventions. Adding this
 * extractor suppresses nested HTTP client instrumentations similarly to how using {@link
 * HttpClientAttributesExtractor} would.
 */
class CoralNestedHttpClientSuppressionAttributesExtractor<REQUEST, RESPONSE>
    implements AttributesExtractor<REQUEST, RESPONSE>, SpanKeyProvider {

  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, REQUEST request) {}

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      REQUEST request,
      @Nullable RESPONSE response,
      @Nullable Throwable error) {}

  @Nullable
  @Override
  public SpanKey internalGetSpanKey() {
    return SpanKey.HTTP_CLIENT;
  }
}
