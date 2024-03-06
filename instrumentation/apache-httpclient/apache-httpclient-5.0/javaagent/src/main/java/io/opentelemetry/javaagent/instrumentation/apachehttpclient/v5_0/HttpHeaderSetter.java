/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.apachehttpclient.v5_0;

import io.opentelemetry.context.propagation.TextMapSetter;
import javax.annotation.Nullable;
import org.apache.hc.core5.http.HttpRequest;
import java.util.logging.Logger;
import static java.util.logging.Level.WARNING;

enum HttpHeaderSetter implements TextMapSetter<HttpRequest> {
  INSTANCE;

  @Override
  public void set(@Nullable HttpRequest carrier, String key, String value) {
    if (carrier == null) {
      return;
    }
    carrier.setHeader(key, value);
    Logger logger = Logger.getLogger("testing-logger from apache client");
    logger.log(WARNING,"============ key: " + key + "  value: " + value);
  }
}
