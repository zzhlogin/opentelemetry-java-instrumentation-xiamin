/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coralclient;

import com.amazon.coral.service.HttpConstant;
import io.opentelemetry.context.propagation.TextMapSetter;
import com.amazon.coral.service.Job;
import com.amazon.coral.service.http.HttpHeaders;
import java.util.logging.Logger;

import static java.util.logging.Level.WARNING;

import javax.annotation.Nullable;

enum HttpHeaderSetter implements TextMapSetter<Job> {
  INSTANCE;

  @Override
  public void set(@Nullable Job carrier, String key, String value) {
//    Logger logger = Logger.getLogger("testing-logger from coral client");
//    logger.log(WARNING,"============ key: " + key + "  value: " + value);
    if (carrier == null) {
      System.out.println("============ carrier is null , exit");
//      logger.log(WARNING, "============ carrier is null , exit");
      return;
    }

    HttpHeaders headers = carrier.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
    System.out.println("============ key: " + key + "  value: " + value);
//    logger.log(WARNING, "============ key: " + key + "  value: " + value);
    headers.addValue(key, value);
  }
}
