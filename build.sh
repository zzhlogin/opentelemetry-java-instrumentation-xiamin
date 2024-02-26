echo "========build =============="
./gradlew assemble


echo "========push to local maven =============="
./gradlew publishToMavenLocal

# echo "========copy =============="
# cp javaagent/build/libs/opentelemetry-javaagent-1.33.0-SNAPSHOT.jar /home/pinxiang/work/coral-instrumentation/HowtoUseCoral/src/HowtoUseValidation/
# cp javaagent/build/libs/opentelemetry-javaagent-1.33.0-SNAPSHOT.jar /home/pinxiang/work/coral-instrumentation/HowtoUseCoralRemoteServer/src/HowtoUseValidation/
