plugins {
  id("otel.library-instrumentation")
}

base.archivesName.set("${base.archivesName.get()}-autoconfigure")

dependencies {
  compileOnly(project(":javaagent-extension-api"))

  implementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:library"))

  library("software.amazon.awssdk:aws-core:2.2.0")
  library("software.amazon.awssdk:aws-json-protocol:2.2.0")

  testImplementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:testing"))

  testLibrary("software.amazon.awssdk:dynamodb:2.2.0")
  testLibrary("software.amazon.awssdk:ec2:2.2.0")
  testLibrary("software.amazon.awssdk:kinesis:2.2.0")
  testLibrary("software.amazon.awssdk:rds:2.2.0")
  testLibrary("software.amazon.awssdk:s3:2.2.0")
  testLibrary("software.amazon.awssdk:sqs:2.2.0")
  testLibrary("software.amazon.awssdk:sns:2.2.0")
  testLibrary("software.amazon.awssdk:secretsmanager:2.2.0")
  testLibrary("software.amazon.awssdk:sfn:2.2.0")
  testLibrary("software.amazon.awssdk:lambda:2.2.0")

  // last version that does not use json protocol
  latestDepTestLibrary("software.amazon.awssdk:sqs:2.21.17")
}

tasks {
  test {
    systemProperty("otel.instrumentation.aws-sdk.experimental-span-attributes", true)
    systemProperty("otel.instrumentation.aws-sdk.experimental-use-propagator-for-messaging", true)
    systemProperty("otel.instrumentation.aws-sdk.experimental-record-individual-http-error", true)
    systemProperty("otel.instrumentation.messaging.experimental.capture-headers", "test-message-header")
  }
}
