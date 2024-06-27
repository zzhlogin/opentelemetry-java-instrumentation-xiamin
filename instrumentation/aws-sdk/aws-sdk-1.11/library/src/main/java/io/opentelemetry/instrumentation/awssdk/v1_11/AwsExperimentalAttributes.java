/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

import io.opentelemetry.api.common.AttributeKey;

final class AwsExperimentalAttributes {
  static final AttributeKey<String> AWS_AGENT = stringKey("aws.agent");
  static final AttributeKey<String> AWS_ENDPOINT = stringKey("aws.endpoint");
  static final AttributeKey<String> AWS_BUCKET_NAME = stringKey("aws.bucket.name");
  static final AttributeKey<String> AWS_QUEUE_URL = stringKey("aws.queue.url");
  static final AttributeKey<String> AWS_QUEUE_NAME = stringKey("aws.queue.name");
  static final AttributeKey<String> AWS_STREAM_NAME = stringKey("aws.stream.name");
  static final AttributeKey<String> AWS_STREAM_CONSUMER_NAME =
      stringKey("aws.stream.consumer_name");
  static final AttributeKey<String> AWS_TABLE_NAME = stringKey("aws.table.name");
  static final AttributeKey<String> AWS_SECRET_ARN = stringKey("aws.secretsmanager.secret_arn");
  static final AttributeKey<String> AWS_STATE_MACHINE_ARN =
      stringKey("aws.stepfunctions.state_machine_arn");
  static final AttributeKey<String> AWS_ACTIVITY_ARN = stringKey("aws.stepfunctions.activity_arn");
  static final AttributeKey<String> AWS_REQUEST_ID = stringKey("aws.requestId");
  static final AttributeKey<String> AWS_AGENT_ID = stringKey("aws.bedrock.agent_id");
  static final AttributeKey<String> AWS_KNOWLEDGEBASE_ID =
      stringKey("aws.bedrock.knowledgebase_id");
  static final AttributeKey<String> AWS_DATASOURCE_ID = stringKey("aws.bedrock.datasource_id");

  static final AttributeKey<String> AWS_GUARDRAIL_ID = stringKey("aws.bedrock.guardrail_id");
  static final AttributeKey<String> AWS_BEDROCK_RUNTIME_MODEL_ID =
      stringKey("gen_ai.request.model");
  static final AttributeKey<String> AWS_BEDROCK_RUNTIME_TEMPRATURE =
      stringKey("gen_ai.request.temperature");
  static final AttributeKey<String> AWS_BEDROCK_RUNTIME_MAX_TOKEN_COUNT =
      stringKey("gen_ai.request.max_tokens");
  static final AttributeKey<String> AWS_BEDROCK_RUNTIME_TOP_P = stringKey("gen_ai.request.top_p");
  static final AttributeKey<String> AWS_BEDROCK_PROMOT_TOKENS =
      stringKey("gen_ai.usage.prompt_tokens");
  static final AttributeKey<String> AWS_BEDROCK_COMPLETION_TOKENS =
      stringKey("gen_ai.usage.completion_tokens");
  static final AttributeKey<String> AWS_BEDROCK_FINISH_REASONS =
      stringKey("gen_ai.response.finish_reasons");
  static final AttributeKey<String> AWS_BEDROCK_SYSTEM = stringKey("gen_ai.system");

  static final AttributeKey<String> AWS_LAMBDA_FUNCTION_NAME =
      stringKey("aws.lambda.function_name");

  static final AttributeKey<String> AWS_LAMBDA_SOURCE_MAPPING_ID =
      stringKey("aws.lambda.resource_mapping_id");

  private AwsExperimentalAttributes() {}
}
