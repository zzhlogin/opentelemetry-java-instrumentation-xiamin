/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.awssdk.v1_11;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.Request;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import javax.annotation.Nullable;

import java.util.List;
import java.util.Arrays;

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_AGENT_ID;


class BedrockAgentOperation extends AbstractBedrockAgentOperation {
  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, AmazonWebServiceRequest originalRequest){
    System.out.println("BedrockAgentOperation.onStart");
    setAttribute(attributes, AWS_AGENT_ID, originalRequest, RequestAccess::getAgentId);
  };

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      @Nullable Object awsResps,
      @Nullable Throwable error){
    System.out.println("BedrockAgentOperation.onEnd");
    setAttribute(attributes, AWS_AGENT_ID, awsResps, RequestAccess::getAgentId);
  };

  @Override
  public List<String> requestClassNames() {
    return Arrays.asList(
        "CreateAgentActionGroupRequest",
        "CreateAgentAliasRequest",
        "DeleteAgentActionGroupRequest",
        "DeleteAgentAliasRequest",
        "DeleteAgentRequest",
        "DeleteAgentVersionRequest",
        "GetAgentActionGroupRequest",
        "GetAgentAliasRequest",
        "GetAgentRequest",
        "GetAgentVersionRequest",
        "ListAgentActionGroupsRequest",
        "ListAgentAliasesRequest",
        "ListAgentKnowledgeBasesRequest",
        "ListAgentVersionsRequest",
        "PrepareAgentRequest",
        "UpdateAgentActionGroupRequest",
        "UpdateAgentAliasRequest",
        "UpdateAgentRequest"
    );
  }

  @Override
  public List<String> responseClassNames() {
    return Arrays.asList(
        "CreateAgentActionGroupResult",
        "CreateAgentAliasResult",
        "DeleteAgentActionGroupResult",
        "DeleteAgentAliasResult",
        "DeleteAgentResult",
        "DeleteAgentVersionResult",
        "GetAgentActionGroupResult",
        "GetAgentAliasResult",
        "GetAgentResult",
        "GetAgentVersionResult",
        "ListAgentActionGroupsResult",
        "ListAgentAliasesResult",
        "ListAgentKnowledgeBasesResult",
        "ListAgentVersionsResult",
        "PrepareAgentResult",
        "UpdateAgentActionGroupResult",
        "UpdateAgentAliasResult",
        "UpdateAgentResult"
    );
  }
}
