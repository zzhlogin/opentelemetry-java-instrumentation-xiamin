val stableVersion = "1.33.6-adot1"
val alphaVersion = "1.33.6-adot2-alpha"

allprojects {
  if (findProperty("otel.stable") != "true") {
    version = alphaVersion
  } else {
    version = stableVersion
  }
}
