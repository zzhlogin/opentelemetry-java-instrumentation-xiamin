/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_AGENT;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_AGENT_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_RUNTIME_MODEL_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BEDROCK_SYSTEM;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BUCKET_NAME;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_ENDPOINT;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_GUARDRAIL_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_KNOWLEDGEBASE_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_QUEUE_NAME;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_QUEUE_URL;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_REQUEST_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_STREAM_NAME;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_TABLE_NAME;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.Request;
import com.amazonaws.Response;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;

class AwsSdkExperimentalAttributesExtractor
    implements AttributesExtractor<Request<?>, Response<?>> {
  private static final String COMPONENT_NAME = "java-aws-sdk";
  private static final String BEDROCK_SERVICE = "AmazonBedrock";
  private static final String BEDROCK_AGENT_SERVICE = "AWSBedrockAgent";
  private static final String BEDROCK_AGENT_RUNTIME_SERVICE = "AWSBedrockAgentRuntime";
  private static final String BEDROCK_RUNTIME_SERVICE = "AmazonBedrockRuntime";
  private String serviceName;

  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, Request<?> request) {
    attributes.put(AWS_AGENT, COMPONENT_NAME);
    attributes.put(AWS_ENDPOINT, request.getEndpoint().toString());

    serviceName = request.getServiceName();
    Object originalRequest = request.getOriginalRequest();
    String requestClassName = originalRequest.getClass().getSimpleName();
    setAttribute(attributes, AWS_BUCKET_NAME, originalRequest, RequestAccess::getBucketName);
    setAttribute(attributes, AWS_QUEUE_URL, originalRequest, RequestAccess::getQueueUrl);
    setAttribute(attributes, AWS_QUEUE_NAME, originalRequest, RequestAccess::getQueueName);
    setAttribute(attributes, AWS_STREAM_NAME, originalRequest, RequestAccess::getStreamName);
    setAttribute(attributes, AWS_TABLE_NAME, originalRequest, RequestAccess::getTableName);

    switch (serviceName) {
      case BEDROCK_SERVICE:
        setAttribute(attributes, AWS_GUARDRAIL_ID, originalRequest, RequestAccess::getGuardrailId);
        break;
      case BEDROCK_AGENT_SERVICE:
        AwsResourceType resourceType = AwsAttributeMap.getRequestType(requestClassName);
        if (resourceType != null) {
          setAttribute(
              attributes,
              resourceType.getKeyAttribute(),
              originalRequest,
              resourceType.getGetter());
        }
        break;
      case BEDROCK_AGENT_RUNTIME_SERVICE:
        setAttribute(attributes, AWS_AGENT_ID, originalRequest, RequestAccess::getAgentId);
        setAttribute(
            attributes, AWS_KNOWLEDGEBASE_ID, originalRequest, RequestAccess::getKnowledgeBaseId);
        break;
      case BEDROCK_RUNTIME_SERVICE:
        if (!Objects.equals(requestClassName, "InvokeModelRequest")) {
          break;
        }
        attributes.put(AWS_BEDROCK_SYSTEM, "aws_bedrock");
        Function<Object, String> getter = RequestAccess::getModelId;
        String modelId = getter.apply(originalRequest);
        attributes.put(AWS_BEDROCK_RUNTIME_MODEL_ID, modelId);
        break;
      default:
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
    if (response != null) {
      Object awsResp = response.getAwsResponse();
      if (awsResp instanceof AmazonWebServiceResponse) {
        AmazonWebServiceResponse<?> awsWebServiceResponse = (AmazonWebServiceResponse<?>) awsResp;
        String requestId = awsWebServiceResponse.getRequestId();
        if (requestId != null) {
          attributes.put(AWS_REQUEST_ID, requestId);
        }
      }

      if (awsResp != null) {
        switch (serviceName) {
          case BEDROCK_SERVICE:
            setAttribute(attributes, AWS_GUARDRAIL_ID, awsResp, RequestAccess::getGuardrailId);
            break;
          case BEDROCK_AGENT_SERVICE:
            String responseClassName = awsResp.getClass().getSimpleName();
            AwsResourceType resourceType = AwsAttributeMap.getReponseType(responseClassName);
            if (resourceType != null) {
              setAttribute(
                  attributes, resourceType.getKeyAttribute(), awsResp, resourceType.getGetter());
            }
            break;
          case BEDROCK_AGENT_RUNTIME_SERVICE:
            setAttribute(attributes, AWS_AGENT_ID, awsResp, RequestAccess::getAgentId);
            setAttribute(
                attributes, AWS_KNOWLEDGEBASE_ID, awsResp, RequestAccess::getKnowledgeBaseId);
            break;
          default:
            break;
        }
      }
    }
  }

  private static void setAttribute(
      AttributesBuilder attributes,
      AttributeKey<String> key,
      Object request,
      Function<Object, String> getter) {
    String value = getter.apply(request);
    if (value != null) {
      attributes.put(key, value);
    }
  }
}
