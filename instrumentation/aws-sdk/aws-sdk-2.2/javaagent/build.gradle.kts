plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  pass {
    group.set("software.amazon.awssdk")
    module.set("aws-core")
    versions.set("[2.2.0,)")
    // Used by all SDK services, the only case it isn't is an SDK extension such as a custom HTTP
    // client, which is not target of instrumentation anyways.
    extraDependency("software.amazon.awssdk:protocol-core")

    excludeInstrumentationName("aws-sdk-2.2-sqs")
    excludeInstrumentationName("aws-sdk-2.2-sns")

    // several software.amazon.awssdk artifacts are missing for this version
    skip("2.17.200")
  }

  fail {
    group.set("software.amazon.awssdk")
    module.set("aws-core")
    versions.set("[2.2.0,)")
    // Used by all SDK services, the only case it isn't is an SDK extension such as a custom HTTP
    // client, which is not target of instrumentation anyways.
    extraDependency("software.amazon.awssdk:protocol-core")

    // "fail" asserts that *all* the instrumentation modules fail to load, but the core one is
    // actually expected to succeed, so exclude it from checks.
    excludeInstrumentationName("aws-sdk-2.2-core")

    // several software.amazon.awssdk artifacts are missing for this version
    skip("2.17.200")
  }

  pass {
    group.set("software.amazon.awssdk")
    module.set("sqs")
    versions.set("[2.2.0,)")
    // Used by all SDK services, the only case it isn't is an SDK extension such as a custom HTTP
    // client, which is not target of instrumentation anyways.
    extraDependency("software.amazon.awssdk:protocol-core")

    excludeInstrumentationName("aws-sdk-2.2-sns")

    // several software.amazon.awssdk artifacts are missing for this version
    skip("2.17.200")
  }

  pass {
    group.set("software.amazon.awssdk")
    module.set("sns")
    versions.set("[2.2.0,)")
    // Used by all SDK services, the only case it isn't is an SDK extension such as a custom HTTP
    // client, which is not target of instrumentation anyways.
    extraDependency("software.amazon.awssdk:protocol-core")

    excludeInstrumentationName("aws-sdk-2.2-sqs")

    // several software.amazon.awssdk artifacts are missing for this version
    skip("2.17.200")
  }
}

dependencies {
  implementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:library-autoconfigure"))
  implementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:library"))

  library("software.amazon.awssdk:aws-core:2.2.0")
  library("software.amazon.awssdk:sqs:2.2.0")

  testImplementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:testing"))
  // Make sure these don't add HTTP headers
  testInstrumentation(project(":instrumentation:apache-httpclient:apache-httpclient-4.0:javaagent"))
  testInstrumentation(project(":instrumentation:apache-httpclient:apache-httpclient-5.0:javaagent"))
  testInstrumentation(project(":instrumentation:netty:netty-4.1:javaagent"))

  testLibrary("software.amazon.awssdk:dynamodb:2.2.0")
  testLibrary("software.amazon.awssdk:ec2:2.2.0")
  testLibrary("software.amazon.awssdk:kinesis:2.2.0")
  testLibrary("software.amazon.awssdk:rds:2.2.0")
  testLibrary("software.amazon.awssdk:s3:2.2.0")
  testLibrary("software.amazon.awssdk:sqs:2.2.0")
  testLibrary("software.amazon.awssdk:sns:2.2.0")
  testLibrary("software.amazon.awssdk:ses:2.2.0")
  testLibrary("software.amazon.awssdk:sfn:2.2.0")
  testLibrary("software.amazon.awssdk:secretsmanager:2.2.0")
  testLibrary("software.amazon.awssdk:lambda:2.2.0")

  // last version that does not use json protocol
  latestDepTestLibrary("software.amazon.awssdk:sqs:2.21.17")
}

val latestDepTest = findProperty("testLatestDeps") as Boolean

testing {
  suites {
    val s3PresignerTest by registering(JvmTestSuite::class) {
      dependencies {
        if (latestDepTest) {
          implementation("software.amazon.awssdk:s3:+")
        } else {
          implementation("software.amazon.awssdk:s3:2.10.12")
        }
        implementation(project(":instrumentation:aws-sdk:aws-sdk-2.2:library"))
      }
    }
  }
}

tasks {
  val testExperimentalSqs by registering(Test::class) {
    filter {
      excludeTestsMatching("Aws2SqsSuppressReceiveSpansTest")
    }
    systemProperty("otel.instrumentation.aws-sdk.experimental-use-propagator-for-messaging", "true")
    systemProperty("otel.instrumentation.messaging.experimental.receive-telemetry.enabled", "true")
  }

  val testReceiveSpansDisabled by registering(Test::class) {
    filter {
      includeTestsMatching("Aws2SqsSuppressReceiveSpansTest")
    }
    include("**/Aws2SqsSuppressReceiveSpansTest.*")
  }

  test {
    filter {
      excludeTestsMatching("Aws2SqsSuppressReceiveSpansTest")
    }
    systemProperty("otel.instrumentation.messaging.experimental.receive-telemetry.enabled", "true")
  }

  check {
    dependsOn(testExperimentalSqs)
    dependsOn(testReceiveSpansDisabled)
    dependsOn(testing.suites)
  }

  withType<Test>().configureEach {
    // TODO run tests both with and without experimental span attributes
    systemProperty("otel.instrumentation.aws-sdk.experimental-span-attributes", "true")
    systemProperty("otel.instrumentation.aws-sdk.experimental-record-individual-http-error", "true")
    systemProperty("testLatestDeps", findProperty("testLatestDeps") as Boolean)
  }

  withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>().configureEach {
    mergeServiceFiles {
      include("software/amazon/awssdk/global/handlers/execution.interceptors")
    }
  }
}
