plugins {
  id("otel.java-conventions")
}

dependencies {
  api(project(":testing-common"))

  api("software.amazon.awssdk:apache-client:2.2.0")
  // older versions don't play nice with armeria http server
  api("software.amazon.awssdk:netty-nio-client:2.11.0")

  // compileOnly because we never want to pin the low version implicitly; need to add dependencies
  // explicitly in user projects, e.g. using testLatestDeps.
  compileOnly("software.amazon.awssdk:dynamodb:2.2.0")
  compileOnly("software.amazon.awssdk:ec2:2.2.0")
  compileOnly("software.amazon.awssdk:kinesis:2.2.0")
  compileOnly("software.amazon.awssdk:rds:2.2.0")
  compileOnly("software.amazon.awssdk:s3:2.2.0")
  compileOnly("software.amazon.awssdk:sqs:2.2.0")
  compileOnly("software.amazon.awssdk:sns:2.2.0")
  compileOnly("software.amazon.awssdk:ses:2.2.0")
  compileOnly("software.amazon.awssdk:secretsmanager:2.2.0")
  compileOnly("software.amazon.awssdk:sfn:2.2.0")
  compileOnly("software.amazon.awssdk:lambda:2.2.0")

  // needed for SQS - using emq directly as localstack references emq v0.15.7 ie WITHOUT AWS trace header propagation
  implementation("org.elasticmq:elasticmq-rest-sqs_2.12:1.0.0")

  implementation("com.google.guava:guava")

  implementation("org.apache.groovy:groovy")
  implementation("io.opentelemetry:opentelemetry-api")
  implementation("org.spockframework:spock-core")
}
