/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_AGENT;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_BUCKET_NAME;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_ENDPOINT;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_QUEUE_NAME;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_QUEUE_URL;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_REQUEST_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_STREAM_CONSUMER_ARN;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_STREAM_NAME;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_TABLE_NAME;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_TOPIC_ARN;

import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.Request;
import com.amazonaws.Response;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import java.util.function.Function;
import javax.annotation.Nullable;

class AwsSdkExperimentalAttributesExtractor
    implements AttributesExtractor<Request<?>, Response<?>> {
  private static final String COMPONENT_NAME = "java-aws-sdk";

  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, Request<?> request) {
    attributes.put(AWS_AGENT, COMPONENT_NAME);
    attributes.put(AWS_ENDPOINT, request.getEndpoint().toString());

    Object originalRequest = request.getOriginalRequest();
    System.out.println("request originalRequest!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println(originalRequest.getClass());
    setAttribute(attributes, AWS_BUCKET_NAME, originalRequest, RequestAccess::getBucketName);
    setAttribute(attributes, AWS_QUEUE_URL, originalRequest, RequestAccess::getQueueUrl);
    setAttribute(attributes, AWS_QUEUE_NAME, originalRequest, RequestAccess::getQueueName);
    setAttribute(attributes, AWS_STREAM_NAME, originalRequest, RequestAccess::getStreamName);
    setAttribute(attributes, AWS_TABLE_NAME, originalRequest, RequestAccess::getTableName);
    setAttribute(
        attributes, AWS_STREAM_CONSUMER_ARN, originalRequest, RequestAccess::getStreamConsumerArn);
    setAttribute(attributes, AWS_TOPIC_ARN, originalRequest, RequestAccess::getTopicArn);
  }

  private static void setAttribute(
      AttributesBuilder attributes,
      AttributeKey<String> key,
      Object object,
      Function<Object, String> getter) {
    String value = getter.apply(object);
    if (value != null) {
      attributes.put(key, value);
    }
  }

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      @Nullable Response<?> response,
      @Nullable Throwable error) {
    System.out.println("result onEnd!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println("result response!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println(response.getClass());
    System.out.println("result awsResp!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println(response.getAwsResponse());
    System.out.println(response.getAwsResponse().getClass());
//    sdk v1 instrumenter.end HERE !!!!!!!!!!!!!!!!!!!!!!!!!
//        result onEnd!!!!!!!!!!!!!!!!!!!!!!!!!
//        result response!!!!!!!!!!!!!!!!!!!!!!!!!
//    class com.amazonaws.Response
//    result awsResp!!!!!!!!!!!!!!!!!!!!!!!!!
//        {ConsumerDescription: {ConsumerName: test_consumer,ConsumerARN: arn:aws:kinesis:us-east-1:007003802740:stream/test_stream/consumer/test_consumer:1717455093,ConsumerStatus: ACTIVE,ConsumerCreationTimestamp: Mon Jun 03 15:51:33 PDT 2024,StreamARN: arn:aws:kinesis:us-east-1:007003802740:stream/test_stream}}
//    class com.amazonaws.services.kinesis.model.DescribeStreamConsumerResult

    Object awsResps = response.getAwsResponse();
    System.out.println("awsResp instanceof AmazonWebServiceResponse!!!!!!!!!!!!!!!!!!!!!!!!!");
    System.out.println(awsResps instanceof AmazonWebServiceResponse);
    System.out.println(awsResps != null);
    setAttribute(attributes, AWS_TOPIC_ARN, awsResps, RequestAccess::getTopicArn);
    System.out.println("result attributes!!!!!!!!!!!!!!!!!!!!!!!!!");
    attributes.build().forEach((key, value) -> System.out.println(key.getKey() + ": " + value));

    if (awsResps instanceof AmazonWebServiceResponse) {
      AmazonWebServiceResponse<?> awsResp = (AmazonWebServiceResponse<?>) awsResps;
      System.out.println("result AmazonWebServiceResponse!!!!!!!!!!!!!!!!!!!!!!!!!");
      String requestId = awsResp.getRequestId();
      System.out.println("result requestId!!!!!!!!!!!!!!!!!!!!!!!!!");
      System.out.println(requestId);
      Object result = awsResp.getResult();
      System.out.println("result outside!!!!!!!!!!!!!!!!!!!!!!!!!");
      if (result != null) {
        System.out.println("result!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println("Result class type: " + result.getClass());
//        System.out.println(result);
      }
      if (requestId != null) {
        attributes.put(AWS_REQUEST_ID, requestId);
      }
    }
  }
}
