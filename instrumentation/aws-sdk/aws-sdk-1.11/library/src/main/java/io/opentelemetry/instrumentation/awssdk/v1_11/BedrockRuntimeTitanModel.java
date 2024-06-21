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
import org.json.JSONArray;

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_FINISH_REASONS;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_TEMPRATURE;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_TOP_P;

class BedrockRuntimeTitanModel extends AbstractBedrockRuntimeModel {
  @Override
  public void onStart(
      AttributesBuilder attributes, Context parentContext, AmazonWebServiceRequest originalRequest){
    System.out.println("BedrockRuntimeTitanModel.onStart");
    Function<Object, ByteBuffer> getter = RequestAccess::getBody;
    ByteBuffer body = getter.apply(originalRequest);
    ByteBuffer resultBodyBuffer = body.asReadOnlyBuffer();
    byte[] bytes = new byte[resultBodyBuffer.remaining()];
    resultBodyBuffer.get(bytes);
    String resultBody = new String(bytes, StandardCharsets.UTF_8);
    JSONObject jsonBody = new JSONObject(resultBody);
    JSONObject textGenerationConfig = jsonBody.getJSONObject("textGenerationConfig");
    if (textGenerationConfig != null) {
      System.out.println("textGenerationConfig!!!!!!!!!!!!!!!");
      if (textGenerationConfig.has("maxTokenCount")) {
        int maxTokenCount = textGenerationConfig.getInt("maxTokenCount");
        System.out.println("maxTokenCount!!!!!!!!!!!!!!!");
        System.out.println(maxTokenCount);
        attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT), maxTokenCount);
      }
      if (textGenerationConfig.has("temperature")) {
        double temperature = textGenerationConfig.getDouble("temperature");
        System.out.println("temperature!!!!!!!!!!!!!!!");
        System.out.println(temperature);
        attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_TEMPRATURE), temperature);
      }
      if (textGenerationConfig.has("topP")) {
        double topP = textGenerationConfig.getDouble("topP");
        System.out.println("topP!!!!!!!!!!!!!!!");
        System.out.println(topP);
        attributes.put(String.valueOf(AWS_BEDROCK_RUNTIME_TOP_P), topP);
      }
    }
  };

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      Object awsResps,
      @Nullable Throwable error){
    System.out.println("BedrockRuntimeTitanModel.onEnd");
    Function<Object, ByteBuffer> getter = RequestAccess::getBody;
    ByteBuffer body = getter.apply(awsResps);
    ByteBuffer resultBodyBuffer = body.asReadOnlyBuffer();
    byte[] bytes = new byte[resultBodyBuffer.remaining()];
    resultBodyBuffer.get(bytes);
    String resultBody = new String(bytes, StandardCharsets.UTF_8);
    JSONObject jsonBody = new JSONObject(resultBody);
    JSONArray results = jsonBody.getJSONArray("results");
    if (results != null) {
      System.out.println("results!!!!!!!!!!!!!!!");
      if (results.getJSONObject(0).has("completionReason")) {
        System.out.println("results.getJSONObject(0).getString(\"completionReason\")!!!!!!!!!!!!!!!");
        String completionReason = results.getJSONObject(0).getString("completionReason");
        System.out.println("completionReason!!!!!!!!!!!!!!!");
        System.out.println(completionReason);
        attributes.put(AWS_BEDROCK_FINISH_REASONS, completionReason);
      }
    }
  };

  @Override
  public List<String> modelNames() {
    return Arrays.asList("amazon.titan");
  }
}
