/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coral;

import io.opentelemetry.instrumentation.api.incubator.semconv.code.CodeAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.internal.ClassNames;

/**
 * A helper {@link SpanNameExtractor} implementation for instrumentations that target specific Java
 * classes/methods.
 */
public final class CoralSpanNameExtractor<REQUEST> implements SpanNameExtractor<REQUEST> {

  /**
   * Returns a {@link SpanNameExtractor} that constructs the span name according to the following
   * pattern: {@code <class.simpleName>.<methodName>}.
   */
  public static <REQUEST> SpanNameExtractor<REQUEST> create(CodeAttributesGetter<REQUEST> getter) {
    return new CoralSpanNameExtractor<>(getter);
  }

  private final CodeAttributesGetter<REQUEST> getter;

  private CoralSpanNameExtractor(CodeAttributesGetter<REQUEST> getter) {
    this.getter = getter;
  }

  @Override
  public String extract(REQUEST request) {
    return getter.getMethodName(request);
  }
}
