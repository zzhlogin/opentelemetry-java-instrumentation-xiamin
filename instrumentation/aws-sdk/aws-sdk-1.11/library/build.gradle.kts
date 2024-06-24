plugins {
  id("otel.library-instrumentation")
}

dependencies {
  implementation("io.opentelemetry.contrib:opentelemetry-aws-xray-propagator")
  implementation("org.json:json") {
    version {
      strictly("[20210307,)")
    }
  }

  library("com.amazonaws:aws-java-sdk-core:1.11.0")
  library("com.amazonaws:aws-java-sdk-sqs:1.11.106")
  compileOnly(project(":muzzle"))

  testImplementation(project(":instrumentation:aws-sdk:aws-sdk-1.11:testing"))

  testLibrary("com.amazonaws:aws-java-sdk-s3:1.11.106")
  testLibrary("com.amazonaws:aws-java-sdk-rds:1.11.106")
  testLibrary("com.amazonaws:aws-java-sdk-ec2:1.11.106")
  testLibrary("com.amazonaws:aws-java-sdk-kinesis:1.11.391")
  testLibrary("com.amazonaws:aws-java-sdk-dynamodb:1.11.106")
  testLibrary("com.amazonaws:aws-java-sdk-sns:1.11.106")
//  testLibrary("com.amazonaws:aws-java-sdk-bedrock:1.12.744")
//  testLibrary("com.amazonaws:aws-java-sdk-bedrockagent:1.12.744")
//  testLibrary("com.amazonaws:aws-java-sdk-bedrockagentruntime:1.12.744")
//  testLibrary("com.amazonaws:aws-java-sdk-bedrockruntime:1.12.744")
  testLibrary("com.amazonaws:aws-java-sdk-secretsmanager:1.11.309")
  testLibrary("com.amazonaws:aws-java-sdk-stepfunctions:1.11.230")
  testLibrary("com.amazonaws:aws-java-sdk-iam:1.11.106")
  testLibrary("com.amazonaws:aws-java-sdk-lambda:1.11.678")

  // last version that does not use json protocol
  latestDepTestLibrary("com.amazonaws:aws-java-sdk-sqs:1.12.583")
}

if (!(findProperty("testLatestDeps") as Boolean)) {
  configurations.testRuntimeClasspath {
    resolutionStrategy {
      eachDependency {
        // early versions of aws sdk are not compatible with jackson 2.16.0
        if (requested.group.startsWith("com.fasterxml.jackson")) {
          useVersion("2.15.3")
        }
      }
    }
  }
}
