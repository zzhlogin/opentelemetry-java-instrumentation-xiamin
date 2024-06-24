plugins {
  id("otel.java-conventions")
}

dependencies {
  api(project(":testing-common"))

  api("com.amazonaws:aws-java-sdk-core:1.11.0")

  compileOnly("com.amazonaws:aws-java-sdk-s3:1.11.106")
  compileOnly("com.amazonaws:aws-java-sdk-rds:1.11.106")
  compileOnly("com.amazonaws:aws-java-sdk-ec2:1.11.106")
  compileOnly("com.amazonaws:aws-java-sdk-kinesis:1.11.391")
  compileOnly("com.amazonaws:aws-java-sdk-dynamodb:1.11.106")
  compileOnly("com.amazonaws:aws-java-sdk-sns:1.11.106")
  compileOnly("com.amazonaws:aws-java-sdk-sqs:1.11.106")
//  compileOnly("com.amazonaws:aws-java-sdk-bedrock:1.12.744")
//  compileOnly("com.amazonaws:aws-java-sdk-bedrockagent:1.12.744")
//  compileOnly("com.amazonaws:aws-java-sdk-bedrockagentruntime:1.12.744")
//  compileOnly("com.amazonaws:aws-java-sdk-bedrockruntime:1.12.744")
  compileOnly("com.amazonaws:aws-java-sdk-secretsmanager:1.11.309")
  compileOnly("com.amazonaws:aws-java-sdk-stepfunctions:1.11.230")
  compileOnly("com.amazonaws:aws-java-sdk-lambda:1.11.678")

  // needed for SQS - using emq directly as localstack references emq v0.15.7 ie WITHOUT AWS trace header propagation
  implementation("org.elasticmq:elasticmq-rest-sqs_2.12:1.0.0")

  implementation("com.google.guava:guava")

  implementation("org.apache.groovy:groovy")
  implementation("io.opentelemetry:opentelemetry-api")
  implementation("org.spockframework:spock-core")
}
