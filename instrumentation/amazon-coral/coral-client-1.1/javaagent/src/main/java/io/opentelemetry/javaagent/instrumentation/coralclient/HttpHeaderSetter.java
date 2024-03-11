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
    if (carrier == null) {
      return;
    }

    HttpHeaders headers = carrier.getRequest().getAttribute(HttpConstant.HTTP_HEADERS);
    headers.addValue(key, value);
  }
}
