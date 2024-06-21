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

import static io.opentelemetry.instrumentation.awssdk.v1_11.AwsExperimentalAttributes.AWS_DATASOURCE_ID;

class BedrockDataSourceOperation extends AbstractBedrockAgentOperation {
  @Override
  public void onStart(AttributesBuilder attributes, Context parentContext, AmazonWebServiceRequest originalRequest){
    System.out.println("BedrockDataSourceOperation.onStart");
    setAttribute(attributes, AWS_DATASOURCE_ID, originalRequest, RequestAccess::getDataSourceId);
  };

  @Override
  public void onEnd(
      AttributesBuilder attributes,
      Context context,
      Request<?> request,
      Object awsResps,
      @Nullable Throwable error){
    System.out.println("BedrockDataSourceOperation.onEnd");
    setAttribute(attributes, AWS_DATASOURCE_ID, awsResps, RequestAccess::getDataSourceId);
  };

  @Override
  public List<String> requestClassNames() {
    return Arrays.asList("DeleteDataSourceRequest", "GetDataSourceRequest", "UpdateDataSourceRequest");
  }

  @Override
  public List<String> responseClassNames() {
    return Arrays.asList("DeleteDataSourceResult", "GetDataSourceResult", "UpdateDataSourceResult");
  }
}
