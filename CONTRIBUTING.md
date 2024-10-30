# Contributing

Pull requests for bug fixes are always welcome!

Before submitting new features or changes to current functionality, it is recommended to first
[open an issue](https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/new)
and discuss your ideas or propose the changes you wish to make.

## Building

This project requires Java 17 to build and run tests. Newer JDK's may work, but this version is used in CI.

Some instrumentations and tests may put constraints on which java versions they support.
See [Running the tests](./docs/contributing/running-tests.md) for more details.

### Snapshot builds

For developers testing code changes before a release is complete, there are
snapshot builds of the `main` branch. They are available from
the Sonatype OSS snapshots repository at `https://oss.sonatype.org/content/repositories/snapshots/`
([browse](https://oss.sonatype.org/content/repositories/snapshots/io/opentelemetry/))

### Building from source

Build using Java 17:

```bash
java -version
```

```bash
./gradlew assemble
```

and then you can find the java agent artifact at

`javaagent/build/libs/opentelemetry-javaagent-<version>.jar`.

To simplify local development, you can remove the version number from the build product. This allows
the file name to stay consistent across versions. To do so, add the following to
`~/.gradle/gradle.properties`.

```properties
removeJarVersionNumbers=true
```

## IntelliJ setup and troubleshooting

See [IntelliJ setup and troubleshooting](docs/contributing/intellij-setup-and-troubleshooting.md)

## Style guide

See [Style guide](docs/contributing/style-guideline.md)

## Running the tests

See [Running the tests](docs/contributing/running-tests.md)

## Writing instrumentation

See [Writing instrumentation](docs/contributing/writing-instrumentation.md)

## Understanding the javaagent structure

See [Understanding the javaagent structure](docs/contributing/javaagent-structure.md)

## Understanding the javaagent instrumentation testing components

See [Understanding the javaagent instrumentation testing components](docs/contributing/javaagent-test-infra.md)

## Debugging

See [Debugging](docs/contributing/debugging.md)

## Understanding Muzzle

See [Understanding Muzzle](docs/contributing/muzzle.md)

## Troubleshooting PR build failures

The build logs are very long and there is a lot of parallelization, so the logs can be hard to
decipher, but if you expand the "Build scan" step, you should see something like:

```
Run cat build-scan.txt
https://gradle.com/s/ila4qwp5lcf5s
```

Opening the build scan link can sometimes take several seconds (it's a large build), but it
typically makes it a lot clearer what's failing.
