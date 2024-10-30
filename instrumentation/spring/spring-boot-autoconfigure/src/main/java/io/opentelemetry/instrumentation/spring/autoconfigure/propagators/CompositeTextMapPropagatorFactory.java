/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.autoconfigure.propagators;

import static java.util.logging.Level.WARNING;

import io.opentelemetry.api.baggage.propagation.W3CBaggagePropagator;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.contrib.awsxray.propagator.AwsXrayPropagator;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.extension.trace.propagation.OtTracePropagator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.ClassUtils;

/** Factory of composite {@link TextMapPropagator}. Defaults to W3C and BAGGAGE. */
public final class CompositeTextMapPropagatorFactory {

  private static final Logger logger =
      Logger.getLogger(CompositeTextMapPropagatorFactory.class.getName());

  @SuppressWarnings("deprecation") // deprecated class to be updated once published in new location
  static TextMapPropagator getCompositeTextMapPropagator(
      BeanFactory beanFactory, List<String> types) {

    Set<TextMapPropagator> propagators = new HashSet<>();

    for (String type : types) {
      switch (type) {
        case "b3":
          if (isOnClasspath("io.opentelemetry.extension.trace.propagation.B3Propagator")) {
            propagators.add(
                beanFactory
                    .getBeanProvider(B3Propagator.class)
                    .getIfAvailable(B3Propagator::injectingSingleHeader));
          }
          break;
        case "b3multi":
          if (isOnClasspath("io.opentelemetry.extension.trace.propagation.B3Propagator")) {
            propagators.add(
                beanFactory
                    .getBeanProvider(B3Propagator.class)
                    .getIfAvailable(B3Propagator::injectingMultiHeaders));
          }
          break;
        case "ottrace":
          if (isOnClasspath("io.opentelemetry.extension.trace.propagation.OtTracerPropagator")) {
            propagators.add(
                beanFactory
                    .getBeanProvider(OtTracePropagator.class)
                    .getIfAvailable(OtTracePropagator::getInstance));
          }
          break;
        case "xray":
          if (isOnClasspath("io.opentelemetry.contrib.awsxray.AwsXrayPropagator")) {
            propagators.add(
                beanFactory
                    .getBeanProvider(AwsXrayPropagator.class)
                    .getIfAvailable(AwsXrayPropagator::getInstance));
          }
          break;
        case "tracecontext":
          propagators.add(W3CTraceContextPropagator.getInstance());
          break;
        case "baggage":
          propagators.add(W3CBaggagePropagator.getInstance());
          break;
        default:
          logger.log(WARNING, "Unsupported type of propagator: {0}", type);
          break;
      }
    }

    return TextMapPropagator.composite(propagators);
  }

  private static boolean isOnClasspath(String clazz) {
    return ClassUtils.isPresent(clazz, null);
  }

  private CompositeTextMapPropagatorFactory() {}
}
