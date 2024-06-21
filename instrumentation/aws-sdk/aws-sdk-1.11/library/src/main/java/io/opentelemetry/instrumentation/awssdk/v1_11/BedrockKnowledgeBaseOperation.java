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

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_KNOWLEDGEBASE_ID;

class BedrockKnowledgeBaseOperation extends AbstractBedrockAgentOperation {
  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, AmazonWebServiceRequest originalRequest){
    setAttribute(attributes, AWS_KNOWLEDGEBASE_ID, originalRequest, RequestAccess::getKnowledgeBaseId);
  };

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      Object awsResps,
      @Nullable Throwable error){
    setAttribute(attributes, AWS_KNOWLEDGEBASE_ID, awsResps, RequestAccess::getKnowledgeBaseId);
  };

  @Override
  public List<String> requestClassNames() {
    return Arrays.asList(
        "AssociateAgentKnowledgeBaseRequest",
        "CreateDataSourceRequest",
        "DeleteKnowledgeBaseRequest",
        "DisassociateAgentKnowledgeBaseRequest",
        "GetAgentKnowledgeBaseRequest",
        "GetKnowledgeBaseRequest",
        "ListDataSourcesRequest",
        "UpdateAgentKnowledgeBaseRequest"
    );
  }

  @Override
  public List<String> responseClassNames() {
    return Arrays.asList(
        "DeleteKnowledgeBaseResult"
    );
  }
}
