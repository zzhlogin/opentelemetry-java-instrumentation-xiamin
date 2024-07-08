/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_AGENT_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_DATASOURCE_ID;
import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_KNOWLEDGEBASE_ID;

import io.opentelemetry.api.common.AttributeKey;
import java.util.function.Function;

enum AwsResourceType {
  AGENT_TYPE(AWS_AGENT_ID, RequestAccess::getAgentId),
  DATA_SOURCE_TYPE(AWS_DATASOURCE_ID, RequestAccess::getDataSourceId),
  KNOWLEDGE_BASE_TYPE(AWS_KNOWLEDGEBASE_ID, RequestAccess::getKnowledgeBaseId);

  @SuppressWarnings("ImmutableEnumChecker")
  private final AttributeKey<String> keyAttribute;

  @SuppressWarnings("ImmutableEnumChecker")
  private final Function<Object, String> getter;

  AwsResourceType(AttributeKey<String> keyAttribute, Function<Object, String> getter) {
    this.keyAttribute = keyAttribute;
    this.getter = getter;
  }

  public AttributeKey<String> getKeyAttribute() {
    return keyAttribute;
  }

  public Function<Object, String> getGetter() {
    return getter;
  }
}
