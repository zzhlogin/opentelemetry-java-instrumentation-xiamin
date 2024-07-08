/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import java.util.HashMap;
import java.util.Map;

final class AwsAttributeMap {
  private static final Map<String, AwsResourceType> BEDROCK_REQUEST_MAP = new HashMap<>();
  private static final Map<String, AwsResourceType> BEDROCK_RESPONSE_MAP = new HashMap<>();

  private AwsAttributeMap() {}

  static {
    // Bedrock request/response mapping
    // We only support operations that are related to the resource and where the context contains
    // the resource ID.
    BEDROCK_REQUEST_MAP.put("CreateAgentActionGroupRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("CreateAgentAliasRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("DeleteAgentActionGroupRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("DeleteAgentAliasRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("DeleteAgentRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("DeleteAgentVersionRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("GetAgentActionGroupRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("GetAgentAliasRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("GetAgentRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("GetAgentVersionRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("ListAgentActionGroupsRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("ListAgentAliasesRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("ListAgentKnowledgeBasesRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("ListAgentVersionsRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("PrepareAgentRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("UpdateAgentActionGroupRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("UpdateAgentAliasRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("UpdateAgentRequest", AwsResourceType.AGENT_TYPE);
    BEDROCK_REQUEST_MAP.put("DeleteDataSourceRequest", AwsResourceType.DATA_SOURCE_TYPE);
    BEDROCK_REQUEST_MAP.put("GetDataSourceRequest", AwsResourceType.DATA_SOURCE_TYPE);
    BEDROCK_REQUEST_MAP.put("UpdateDataSourceRequest", AwsResourceType.DATA_SOURCE_TYPE);
    BEDROCK_REQUEST_MAP.put(
        "AssociateAgentKnowledgeBaseRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_REQUEST_MAP.put("CreateDataSourceRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_REQUEST_MAP.put("DeleteKnowledgeBaseRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_REQUEST_MAP.put(
        "DisassociateAgentKnowledgeBaseRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_REQUEST_MAP.put("GetAgentKnowledgeBaseRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_REQUEST_MAP.put("GetKnowledgeBaseRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_REQUEST_MAP.put("ListDataSourcesRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_REQUEST_MAP.put("UpdateAgentKnowledgeBaseRequest", AwsResourceType.KNOWLEDGE_BASE_TYPE);
    BEDROCK_RESPONSE_MAP.put("DeleteAgentAliasResult", AwsResourceType.AGENT_TYPE);
    BEDROCK_RESPONSE_MAP.put("DeleteAgentResult", AwsResourceType.AGENT_TYPE);
    BEDROCK_RESPONSE_MAP.put("DeleteAgentVersionResult", AwsResourceType.AGENT_TYPE);
    BEDROCK_RESPONSE_MAP.put("PrepareAgentResult", AwsResourceType.AGENT_TYPE);
    BEDROCK_RESPONSE_MAP.put("DeleteDataSourceResult", AwsResourceType.DATA_SOURCE_TYPE);
    BEDROCK_RESPONSE_MAP.put("DeleteKnowledgeBaseResult", AwsResourceType.KNOWLEDGE_BASE_TYPE);
  }

  public static AwsResourceType getRequestType(String requestClass) {
    return BEDROCK_REQUEST_MAP.get(requestClass);
  }

  public static AwsResourceType getReponseType(String requestClass) {
    return BEDROCK_RESPONSE_MAP.get(requestClass);
  }
}
