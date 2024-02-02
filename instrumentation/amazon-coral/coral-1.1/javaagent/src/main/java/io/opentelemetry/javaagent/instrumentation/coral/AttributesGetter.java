/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.coral;

import io.opentelemetry.instrumentation.api.incubator.semconv.code.CodeAttributesExtractor;
import javax.annotation.Nullable;

/**
 * An interface for getting code attributes.
 *
 * <p>Instrumentation authors will create implementations of this interface for their specific
 * library/framework. It will be used by the {@link CodeAttributesExtractor} to obtain the various
 * code attributes in a type-generic way.
 */
public interface AttributesGetter<REQUEST> {

  @Nullable
  Class<?> getCodeClass(REQUEST request);

  @Nullable
  String getMethodName(REQUEST request);

  String getServerAddress(REQUEST request);

  String getUrlPath(REQUEST request);

  String getClientAddress(REQUEST request);

  String getHttpMethod(REQUEST request);

  String getUserAgent(REQUEST request);

  String getHttpSchema(REQUEST request);

  String getNetPeerIp(REQUEST request);

  String getServiceName(REQUEST request);

  Integer getHttpResponseStatusCode(REQUEST request);
}
