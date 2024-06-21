/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.instrumentation.awssdk.v1_11;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.http.HttpResponse;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;


import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_AGENT_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_KNOWLEDGEBASE_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_GUARDRAIL_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_MODEL_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_SYSTEM;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_PROMOT_TOKENS;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_COMPLETION_TOKENS;

public class BedrockAttributesExtractor implements AttributesExtractor<Request<?>, Response<?>> {
  private String serviceName;
  private String modelName;
  private static final Map<String, Class<? extends AbstractBedrockAgentOperation>> REQUEST_CLASS_MAPPING;
  private static final Map<String, Class<? extends AbstractBedrockAgentOperation>> RESPONSE_CLASS_MAPPING;
  private static final Map<String, Class<? extends AbstractBedrockRuntimeModel>> BEDROC_RUNTIME_MODEL_MAPPING;

  static {
    List<Class<? extends AbstractBedrockAgentOperation>> operations = Arrays.asList(
        BedrockKnowledgeBaseOperation.class,
        BedrockDataSourceOperation.class,
        BedrockAgentOperation.class
    );

    List<Class<? extends AbstractBedrockRuntimeModel>> models = Arrays.asList(
        BedrockRuntimeClaudeModel.class,
        BedrockRuntimeLlamaModel.class,
        BedrockRuntimeTitanModel.class
    );

    REQUEST_CLASS_MAPPING = operations.stream()
        .flatMap(opClass -> {
          try {
            return opClass.getDeclaredConstructor().newInstance().requestClassNames().stream()
                .collect(Collectors.toMap(requestClassName -> requestClassName, requestClassName -> opClass)).entrySet().stream();
          } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Failed to instantiate operation class", e);
          }
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    RESPONSE_CLASS_MAPPING = operations.stream()
        .flatMap(opClass -> {
          try {
            return opClass.getDeclaredConstructor().newInstance().responseClassNames().stream()
                .collect(Collectors.toMap(responseClassName -> responseClassName, responseClassName -> opClass)).entrySet().stream();
          } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Failed to instantiate operation class", e);
          }
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    BEDROC_RUNTIME_MODEL_MAPPING = models.stream()
        .flatMap(modelClass -> {
          try {
            return modelClass.getDeclaredConstructor().newInstance().modelNames().stream()
                .collect(Collectors.toMap(modelName -> modelName, modelName -> modelClass)).entrySet().stream();
          } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Failed to instantiate operation class", e);
          }
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, Request<?> request) {
    System.out.println("BedrockAttributesExtractor.onStart!!!!!!!!!!!!!!!");
    serviceName = request.getServiceName();
    System.out.println("request.getServiceName()!!!!!!!!!!!!!!!"); // AmazonBedrock, AWSBedrockAgent, AWSBedrockAgentRuntime
    System.out.println(serviceName);
    AmazonWebServiceRequest originalRequest = request.getOriginalRequest();
    System.out.println("originalRequest.getClass().getName()!!!!!!!!!!!!!!!");
    System.out.println(originalRequest.getClass().getName());
    String[] requestClass = originalRequest.getClass().getName().split("\\.");
    switch (serviceName) {
      case "AmazonBedrock":
        setAttribute(attributes, AWS_GUARDRAIL_ID, originalRequest, RequestAccess::getGuardrailId);
        // Perform action for AmazonBedrock
        break;
      case "AWSBedrockAgent":
        Class<? extends AbstractBedrockAgentOperation> operationClass = REQUEST_CLASS_MAPPING.get(requestClass[requestClass.length - 1]);
        if (operationClass != null) {
          try {
            operationClass.getDeclaredConstructor().newInstance().onStart(attributes, parentContext, originalRequest);
          } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                   InvocationTargetException e) {
            throw new IllegalStateException("Failed to instantiate operation class", e);
          }
        }
        break;
      case "AWSBedrockAgentRuntime":
        setAttribute(attributes, AWS_AGENT_ID, originalRequest, RequestAccess::getAgentId);
        setAttribute(attributes, AWS_KNOWLEDGEBASE_ID, originalRequest, RequestAccess::getKnowledgeBaseId);
        break;
      case "AmazonBedrockRuntime":
        // TODO: Implement onStart for AWSBedrockRuntime
        if (!Objects.equals(requestClass[requestClass.length - 1], "InvokeModelRequest")) {
          break;
        }
        attributes.put(AWS_BEDROCK_SYSTEM, "AWS Bedrock");
        Function<Object, String> getter = RequestAccess::getModelId;
        String modelId = getter.apply(originalRequest);
        attributes.put(AWS_BEDROCK_RUNTIME_MODEL_ID, modelId);
        modelName = modelId.split("-")[0];
        System.out.println("modelName!!!!!!!!!!!!!!!");
        System.out.println(modelName);
        Class<? extends AbstractBedrockRuntimeModel> modelClass = BEDROC_RUNTIME_MODEL_MAPPING.get(modelName);
        if (modelClass != null) {
          try {
            modelClass.getDeclaredConstructor().newInstance().onStart(attributes, parentContext, originalRequest);
          } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                   InvocationTargetException e) {
            throw new IllegalStateException("Failed to instantiate operation class", e);
          }
        }
        break;
      default:
        // Perform default action
        break;
    }
  }

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      @Nullable Response<?> response,
      @Nullable Throwable error) {
    System.out.println("BedrockAttributesExtractor.onEnd!!!!!!!!!!!!!!!");
    System.out.println(serviceName);
    if (response != null) {
      System.out.println("response != null!!!!!!!!!!!!!!!");
      Object awsResps = response.getAwsResponse();
      if (awsResps != null) {
        System.out.println("awsResps != null!!!!!!!!!!!!!!!");
        System.out.println(awsResps.getClass().getName());
        System.out.println("awsResps below!!!!!!!!!!!!!!!");
        switch (serviceName) {
          case "AmazonBedrock":
            setAttribute(attributes, AWS_GUARDRAIL_ID, awsResps, RequestAccess::getGuardrailId);
            break;
          case "AWSBedrockAgent":
            System.out.println("awsResps.getClass().getName()!!!!!!!!!!!!!!!");
//            System.out.println(awsResps.getClass().getName()); // com.amazonaws.services.bedrockagent.model.GetDataSourceResult
            String[] responseClass = awsResps.getClass().getName().split("\\.");
            Class<? extends AbstractBedrockAgentOperation> operationClass = RESPONSE_CLASS_MAPPING.get(responseClass[responseClass.length - 1]);
            if (operationClass != null) {
              System.out.println("operationClass.getName()!!!!!!!!!!!!!!!");
              System.out.println(operationClass.getName());
              try {
                operationClass.getDeclaredConstructor().newInstance().onEnd(attributes, context, request, awsResps, error);
              } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                       InvocationTargetException e) {
                throw new IllegalStateException("Failed to instantiate operation class", e);
              }
            }
            break;
          case "AWSBedrockAgentRuntime":
            setAttribute(attributes, AWS_AGENT_ID, awsResps, RequestAccess::getAgentId);
            setAttribute(attributes, AWS_KNOWLEDGEBASE_ID, awsResps, RequestAccess::getKnowledgeBaseId);
            break;
          case "AmazonBedrockRuntime":
            // TODO: Implement onEnd for AWSBedrockRuntime
            HttpResponse httpResps = response.getHttpResponse();
            Map<String, String> headers = httpResps.getHeaders();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
              System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
            if (headers.containsKey("X-Amzn-Bedrock-Input-Token-Count")) {
              int inputTokenCount = Integer.parseInt(headers.get("X-Amzn-Bedrock-Input-Token-Count"));
              System.out.println("X-Amzn-Bedrock-Input-Token-Count!!!!!!!!!!!!!!!");
              System.out.println(inputTokenCount);
              attributes.put(String.valueOf(AWS_BEDROCK_PROMOT_TOKENS), inputTokenCount);
            }
            if (headers.containsKey("X-Amzn-Bedrock-Output-Token-Count")) {
              int outputTokenCount = Integer.parseInt(headers.get("X-Amzn-Bedrock-Output-Token-Count"));
              System.out.println("X-Amzn-Bedrock-Output-Token-Count!!!!!!!!!!!!!!!");
              System.out.println(outputTokenCount);
              attributes.put(String.valueOf(AWS_BEDROCK_COMPLETION_TOKENS), outputTokenCount);
            }

            System.out.println("BEDROC_RUNTIME_MODEL_MAPPING!!!!!!!!!!!!!!!");
            System.out.println(BEDROC_RUNTIME_MODEL_MAPPING);

            Class<? extends AbstractBedrockRuntimeModel> modelClass = BEDROC_RUNTIME_MODEL_MAPPING.get(modelName);
            if (modelClass != null) {
              try {
                modelClass.getDeclaredConstructor().newInstance().onEnd(attributes, context, request, awsResps, error);
              } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                       InvocationTargetException e) {
                throw new IllegalStateException("Failed to instantiate operation class", e);
              }
            }
            break;
          default:
            // Perform default action
            break;
        }
      }
    }
  }

  protected static void setAttribute(
      AttributesBuilder attributes,
      AttributeKey<String> key,
      Object object,
      Function<Object, String> getter) {
    String value = getter.apply(object);
    if (value != null) {
      attributes.put(key, value);
    }
  }
}
