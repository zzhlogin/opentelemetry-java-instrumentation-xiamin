plugins {
  id("otel.javaagent-instrumentation")
}

dependencies {
  implementation(files("../../coral-1.1/javaagent/libs/CoralAttributes-1.1.jar"))
  implementation(files("../../coral-1.1/javaagent/libs/CoralHandler-1.1.jar"))
  implementation(files("../../coral-1.1/javaagent/libs/CoralModel-1.1.jar"))
  implementation(files("../../coral-1.1/javaagent/libs/CoralHttpSupport-1.1.jar"))
  implementation(files("../../coral-1.1/javaagent/libs/CoralMetrics-1.0.jar"))
  implementation(files("../../coral-1.1/javaagent/libs/CoralClient-1.1.jar"))
}
