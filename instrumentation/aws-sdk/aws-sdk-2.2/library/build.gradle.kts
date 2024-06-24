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

  library("software.amazon.awssdk:aws-core:2.2.0")
  library("software.amazon.awssdk:sqs:2.2.0")
  library("software.amazon.awssdk:sns:2.2.0")
  library("software.amazon.awssdk:aws-json-protocol:2.2.0")
  compileOnly(project(":muzzle")) // For @NoMuzzle

  testImplementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:testing"))

  testLibrary("software.amazon.awssdk:dynamodb:2.2.0")
  testLibrary("software.amazon.awssdk:ec2:2.2.0")
  testLibrary("software.amazon.awssdk:kinesis:2.2.0")
  testLibrary("software.amazon.awssdk:rds:2.2.0")
  testLibrary("software.amazon.awssdk:s3:2.2.0")
  testLibrary("software.amazon.awssdk:ses:2.2.0")
  testLibrary("software.amazon.awssdk:secretsmanager:2.2.0")
  testLibrary("software.amazon.awssdk:sfn:2.2.0")
  testLibrary("software.amazon.awssdk:lambda:2.2.0")

  // last version that does not use json protocol
  latestDepTestLibrary("software.amazon.awssdk:sqs:2.21.17")
}

testing {
  suites {
    val testCoreOnly by registering(JvmTestSuite::class) {
      sources {
        groovy {
          setSrcDirs(listOf("src/testCoreOnly/groovy"))
        }
      }

      dependencies {
        implementation(project())
        implementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:testing"))
        compileOnly("software.amazon.awssdk:sqs:2.2.0")
        if (findProperty("testLatestDeps") as Boolean) {
          implementation("software.amazon.awssdk:aws-core:+")
          implementation("software.amazon.awssdk:aws-json-protocol:+")
          implementation("software.amazon.awssdk:dynamodb:+")
        } else {
          implementation("software.amazon.awssdk:aws-core:2.2.0")
          implementation("software.amazon.awssdk:aws-json-protocol:2.2.0")
          implementation("software.amazon.awssdk:dynamodb:2.2.0")
        }
      }
    }
  }
}

tasks {
  withType<Test> {
    // NB: If you'd like to change these, there is some cleanup work to be done, as most tests ignore this and
    // set the value directly (the "library" does not normally query it, only library-autoconfigure)
    systemProperty("otel.instrumentation.aws-sdk.experimental-span-attributes", true)
    systemProperty("otel.instrumentation.aws-sdk.experimental-use-propagator-for-messaging", true)
  }
}
