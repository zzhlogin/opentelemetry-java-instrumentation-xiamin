/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coral;

import com.amazon.coral.service.HttpConstant;
import com.amazon.coral.service.Job;
import com.amazon.coral.service.http.HttpHeaders;
import io.opentelemetry.context.propagation.TextMapGetter;
import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

enum HttpHeaderGetter implements TextMapGetter<Job> {
  INSTANCE;

  @Override
  public Iterable<String> keys(Job carrier) {
    HttpHeaders headers = carrier.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
    return StreamSupport.stream(headers.getHeaderNames().spliterator(), false)
        .map(CharSequence::toString)
        .collect(Collectors.toList());
  }

  @Override
  public String get(Job carrier, String key) {
//    Logger logger = Logger.getLogger("testing-logger from coral client");
//    logger.log(WARNING,"============ key: " + key + "  value: " + value);
    if (carrier == null) {
      System.out.println("============ Coral instrumentation - HttpHeaderGetter: carrier is null , exit");
//      logger.log(WARNING, "============ carrier is null , exit");
      return null;
    }

    HttpHeaders headers = carrier.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
//    logger.log(WARNING, "============ key: " + key + "  value: " + value);
    CharSequence value = headers.getValue(key);
    System.out.println("============ Coral instrumentation - HttpHeaderGetter: key: " + key + " value: " + value);
    return value != null ? value.toString() : null;
  }
}
