/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_FINISH_REASONS;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_TEMPRATURE;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_TOP_P;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

class BedrockRuntimeTitanModel extends AbstractBedrockRuntimeModel {
  @Override
  public void onStart(
      AttributesBuilder attributes,
      Context parentContext,
      AmazonWebServiceRequest originalRequest) {
    Function<Object, ByteBuffer> getter = RequestAccess::getBody;
    ByteBuffer body = getter.apply(originalRequest);
    ByteBuffer resultBodyBuffer = body.asReadOnlyBuffer();
    byte[] bytes = new byte[resultBodyBuffer.remaining()];
    resultBodyBuffer.get(bytes);
    String resultBody = new String(bytes, StandardCharsets.UTF_8);
    JSONObject jsonBody = new JSONObject(resultBody);
    JSONObject textGenerationConfig = jsonBody.getJSONObject("textGenerationConfig");
    if (textGenerationConfig != null) {
      if (textGenerationConfig.has("maxTokenCount")) {
        int maxTokenCount = textGenerationConfig.getInt("maxTokenCount");
        attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT), maxTokenCount);
      }
      if (textGenerationConfig.has("temperature")) {
        double temperature = textGenerationConfig.getDouble("temperature");
        attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_TEMPRATURE), temperature);
      }
      if (textGenerationConfig.has("topP")) {
        double topP = textGenerationConfig.getDouble("topP");
        attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_TOP_P), topP);
      }
    }
  }
  ;

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      Object awsResps,
      @Nullable Throwable error) {
    Function<Object, ByteBuffer> getter = RequestAccess::getBody;
    ByteBuffer body = getter.apply(awsResps);
    ByteBuffer resultBodyBuffer = body.asReadOnlyBuffer();
    byte[] bytes = new byte[resultBodyBuffer.remaining()];
    resultBodyBuffer.get(bytes);
    String resultBody = new String(bytes, StandardCharsets.UTF_8);
    JSONObject jsonBody = new JSONObject(resultBody);
    JSONArray results = jsonBody.getJSONArray("results");
    if (results != null) {
      if (results.getJSONObject(0).has("completionReason")) {
        String completionReason = results.getJSONObject(0).getString("completionReason");
        attributes.put(AWS_BEDROCK_FINISH_REASONS, completionReason);
      }
    }
  }
  ;

  @Override
  public List<String> modelNames() {
    return Arrays.asList("amazon.titan");
  }
}
