/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v2_2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.StringUtils;
import org.json.JSONObject;

class Serializer {

  @Nullable
  String serialize(Object target) {

    if (target == null) {
      return null;
    }

    if (target instanceof SdkPojo) {
      return serialize((SdkPojo) target);
    }
    if (target instanceof Collection) {
      return serialize((Collection<?>) target);
    }
    if (target instanceof Map) {
      return serialize(((Map<?, ?>) target).keySet());
    }
    // simple type
    return target.toString();
  }

  @Nullable
  private static String serialize(SdkPojo sdkPojo) {
    ProtocolMarshaller<SdkHttpFullRequest> marshaller =
        AwsJsonProtocolFactoryAccess.createMarshaller();
    if (marshaller == null) {
      return null;
    }
    Optional<ContentStreamProvider> optional = marshaller.marshall(sdkPojo).contentStreamProvider();
    return optional
        .map(
            csp -> {
              try (InputStream cspIs = csp.newStream()) {
                return IoUtils.toUtf8String(cspIs);
              } catch (IOException e) {
                return null;
              }
            })
        .orElse(null);
  }

  private String serialize(Collection<?> collection) {
    String serialized = collection.stream().map(this::serialize).collect(Collectors.joining(","));
    return (StringUtils.isEmpty(serialized) ? null : "[" + serialized + "]");
  }
  @Nullable
  static String serialize(String attributeName, Object target) {
    JSONObject jsonObject;
    if (target instanceof SdkBytes) {
      jsonObject = new JSONObject(((SdkBytes) target).asUtf8String());
    } else {
      if (target != null) {
        return target.toString();
      }
      return null;
    }

    switch (attributeName) {
      case "gen_ai.completion_text":
        return jsonObject.getString("generation");
      case "gen_ai.response.finish_reason":
        return jsonObject.getString("stop_reason");
      case "gen_ai.usage.completion_tokens":
        return String.valueOf(jsonObject.getInt("generation_token_count"));
      case "gen_ai.usage.prompt_tokens":
        return String.valueOf(jsonObject.getInt("prompt_token_count"));
      case "gen_ai.prompt":
        return jsonObject.getString("prompt");
      default:
        return null;
    }
  }

}
