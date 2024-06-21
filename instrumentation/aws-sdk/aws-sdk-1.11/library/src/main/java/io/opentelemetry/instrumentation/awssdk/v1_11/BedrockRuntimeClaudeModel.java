package io.opentelemetry.instrumentation.awssdk.v1_11;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.json.JSONObject;

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_FINISH_REASONS;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_TEMPRATURE;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_TOP_P;

class BedrockRuntimeClaudeModel extends AbstractBedrockRuntimeModel {
  @Override
  public void onStart(
      AttributesBuilder attributes, Context parentContext, AmazonWebServiceRequest originalRequest){
    System.out.println("BedrockRuntimeClaudeModel.onStart");
    Function<Object, ByteBuffer> getter = RequestAccess::getBody;
    ByteBuffer body = getter.apply(originalRequest);
    ByteBuffer resultBodyBuffer = body.asReadOnlyBuffer();
    byte[] bytes = new byte[resultBodyBuffer.remaining()];
    resultBodyBuffer.get(bytes);
    String resultBody = new String(bytes, StandardCharsets.UTF_8);
    JSONObject jsonBody = new JSONObject(resultBody);
    System.out.println("jsonBody!!!!!!!!!!!!!!!");
    if (jsonBody.has("max_tokens_to_sample")) {
      int maxTokenCount = jsonBody.getInt("max_tokens_to_sample");
      System.out.println("maxTokenCount!!!!!!!!!!!!!!!");
      System.out.println(maxTokenCount);
      attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT), maxTokenCount);
    } else if (jsonBody.has("max_tokens")) {
      int maxTokenCount = jsonBody.getInt("max_tokens");
      System.out.println("maxTokenCount!!!!!!!!!!!!!!!");
      System.out.println(maxTokenCount);
      attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT), maxTokenCount);
    }
    if (jsonBody.has("temperature")) {
      double temperature = jsonBody.getDouble("temperature");
      System.out.println("temperature!!!!!!!!!!!!!!!");
      System.out.println(temperature);
      attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_TEMPRATURE), temperature);
    }
    if (jsonBody.has("top_p")) {
      double topP = jsonBody.getDouble("top_p");
      System.out.println("topP!!!!!!!!!!!!!!!");
      System.out.println(topP);
      attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_TOP_P), topP);
    }
  };

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      Object awsResps,
      @Nullable Throwable error){
    System.out.println("BedrockRuntimeClaudeModel.onEnd");
    Function<Object, ByteBuffer> getter = RequestAccess::getBody;
    ByteBuffer body = getter.apply(awsResps);
    ByteBuffer resultBodyBuffer = body.asReadOnlyBuffer();
    byte[] bytes = new byte[resultBodyBuffer.remaining()];
    resultBodyBuffer.get(bytes);
    String resultBody = new String(bytes, StandardCharsets.UTF_8);
    JSONObject jsonBody = new JSONObject(resultBody);
    System.out.println("jsonBody!!!!!!!!!!!!!!!");
    if (jsonBody.has("stop_reason")) {
      System.out.println("results.getString(\"stop_reason\")!!!!!!!!!!!!!!!");
      String completionReason = jsonBody.getString("stop_reason");
      System.out.println("completionReason!!!!!!!!!!!!!!!");
      System.out.println(completionReason);
      attributes.put(AWS_BEDROCK_FINISH_REASONS, completionReason);
    }
  };

  @Override
  public List<String> modelNames() {
    return Arrays.asList("anthropic.claude");
  }
}
