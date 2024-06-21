/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;

final class RequestAccess {

  private static final ClassValue<RequestAccess> REQUEST_ACCESSORS =
      new ClassValue<RequestAccess>() {
        @Override
        protected RequestAccess computeValue(Class<?> type) {
          return new RequestAccess(type);
        }
      };

  @Nullable
  static String getBucketName(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getBucketName, request);
  }

  @Nullable
  static String getQueueUrl(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getQueueUrl, request);
  }

  @Nullable
  static String getQueueName(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getQueueName, request);
  }

  @Nullable
  static String getStreamName(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getStreamName, request);
  }

  @Nullable
  static String getStreamConsumerName(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getStreamConsumerName, request);
  }

  @Nullable
  static String getTableName(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getTableName, request);
  }

  @Nullable
  static String getAgentId(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getAgentId, request);
  }

  @Nullable
  static String getKnowledgeBaseId(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getKnowledgeBaseId, request);
  }

  @Nullable
  static String getDataSourceId(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getDataSourceId, request);
  }

  @Nullable
  static String getGuardrailId(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getGuardrailId, request);
  }

  @Nullable
  static String getModelId(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getModelId, request);
  }

  @Nullable
  static ByteBuffer getBody(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    try {
      return (ByteBuffer) access.getBody.invoke(request);
    } catch (Throwable t) {
      return null;
    }
  }

  @Nullable
  static String getTopicArn(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getTopicArn, request);
  }

  @Nullable
  static String getTargetArn(Object request) {
    RequestAccess access = REQUEST_ACCESSORS.get(request.getClass());
    return invokeOrNull(access.getTargetArn, request);
  }

  @Nullable
  static String getSecretArn(Object object) {
    if (object == null) {
      return null;
    }
    RequestAccess access = REQUEST_ACCESSORS.get(object.getClass());
    return invokeOrNull(access.getSecretArn, object);
  }

  @Nullable
  static String getResourceEventMappingId(Object object) {
    if (object == null) {
      return null;
    }
    RequestAccess access = REQUEST_ACCESSORS.get(object.getClass());
    return invokeOrNull(access.getResourceEventMappingId, object);
  }

  @Nullable
  static String getFunctionName(Object object) {
    if (object == null) {
      return null;
    }
    RequestAccess access = REQUEST_ACCESSORS.get(object.getClass());
    return invokeOrNull(access.getFunctionName, object);
  }

  @Nullable
  static String getStateMachineArn(Object object) {
    if (object == null) {
      return null;
    }
    RequestAccess access = REQUEST_ACCESSORS.get(object.getClass());
    return invokeOrNull(access.getStateMachineArn, object);
  }

  @Nullable
  static String getActivityArn(Object object) {
    if (object == null) {
      return null;
    }
    RequestAccess access = REQUEST_ACCESSORS.get(object.getClass());
    return invokeOrNull(access.getActivityArn, object);
  }

  @Nullable
  private static String invokeOrNull(@Nullable MethodHandle method, Object obj) {
    if (method == null) {
      return null;
    }
    try {
      return (String) method.invoke(obj);
    } catch (Throwable t) {
      return null;
    }
  }

  @Nullable private final MethodHandle getBucketName;
  @Nullable private final MethodHandle getQueueUrl;
  @Nullable private final MethodHandle getQueueName;
  @Nullable private final MethodHandle getStreamName;
  @Nullable private final MethodHandle getStreamConsumerName;
  @Nullable private final MethodHandle getTableName;
  @Nullable private final MethodHandle getAgentId;
  @Nullable private final MethodHandle getKnowledgeBaseId;
  @Nullable private final MethodHandle getDataSourceId;
  @Nullable private final MethodHandle getGuardrailId;
  @Nullable private final MethodHandle getBody;
  @Nullable private final MethodHandle getModelId;
  @Nullable private final MethodHandle getTopicArn;
  @Nullable private final MethodHandle getTargetArn;
  @Nullable private final MethodHandle getSecretArn;
  @Nullable private final MethodHandle getStateMachineArn;
  @Nullable private final MethodHandle getActivityArn;
  @Nullable private final MethodHandle getFunctionName;
  @Nullable private final MethodHandle getResourceEventMappingId;

  private RequestAccess(Class<?> clz) {
    getBucketName = findAccessorOrNull(clz, "getBucketName", String.class);
    getQueueUrl = findAccessorOrNull(clz, "getQueueUrl", String.class);
    getQueueName = findAccessorOrNull(clz, "getQueueName", String.class);
    getStreamName = findAccessorOrNull(clz, "getStreamName", String.class);
    getTableName = findAccessorOrNull(clz, "getTableName", String.class);
    getStreamConsumerName = findAccessorOrNull(clz, "getConsumerName", String.class);
    getTopicArn = findAccessorOrNull(clz, "getTopicArn", String.class);
    getTargetArn = findAccessorOrNull(clz, "getTargetArn", String.class);
    getSecretArn = findAccessorOrNull(clz, "getARN", String.class);
    getStateMachineArn = findAccessorOrNull(clz, "getStateMachineArn", String.class);
    getActivityArn = findAccessorOrNull(clz, "getActivityArn", String.class);
    getFunctionName = findAccessorOrNull(clz, "getFunctionName", String.class);
    getResourceEventMappingId = findAccessorOrNull(clz, "getUUID", String.class);
    getAgentId = findAccessorOrNull(clz, "getAgentId", String.class);
    getKnowledgeBaseId = findAccessorOrNull(clz, "getKnowledgeBaseId", String.class);
    getDataSourceId = findAccessorOrNull(clz, "getDataSourceId", String.class);
    getGuardrailId = findAccessorOrNull(clz, "getGuardrailId", String.class);
    getBody = findAccessorOrNull(clz, "getBody", ByteBuffer.class);
    getModelId = findAccessorOrNull(clz, "getModelId", String.class);
  }

  @Nullable
  private static MethodHandle findAccessorOrNull(
      Class<?> clz, String methodName, Class<?> returnType) {
    try {
      return MethodHandles.publicLookup()
          .findVirtual(clz, methodName, MethodType.methodType(returnType));
    } catch (Throwable t) {
      return null;
    }
  }
}
