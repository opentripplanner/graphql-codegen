# GraphQL code generator

A build-time tool that generates Java types and data fetcher interfaces for a GraphQL API from its
schema.

## Requirements

Java 25 or newer.

## Usage

Run `org.opentripplanner.graphql.codegen.GraphQLCodegen <config.yml> <output-source-directory>`.
Typically you run it with `exec-maven-plugin` in the `generate-sources` phase, and add the output
directory as a source root with `build-helper-maven-plugin`. Write the output under `target/` so
that it is not committed.

A file is only rewritten when its content changes, so an unchanged schema does not force a
recompile. The generated members follow the schema order. The code is not formatted for readability.

```xml
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.6.4</version>
  <executions>
    <execution>
      <id>graphql-codegen</id>
      <phase>generate-sources</phase>
      <goals>
        <goal>java</goal>
      </goals>
      <configuration>
        <mainClass>org.opentripplanner.graphql.codegen.GraphQLCodegen</mainClass>
        <includeProjectDependencies>false</includeProjectDependencies>
        <includePluginDependencies>true</includePluginDependencies>
        <cleanupDaemonThreads>false</cleanupDaemonThreads>
        <arguments>
          <argument>${project.basedir}/src/build/graphql-codegen.yml</argument>
          <argument>${project.build.directory}/generated-sources/graphql</argument>
        </arguments>
      </configuration>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>org.opentripplanner</groupId>
      <artifactId>graphql-codegen</artifactId>
      <version>${graphql-codegen.version}</version>
    </dependency>
  </dependencies>
</plugin>
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>build-helper-maven-plugin</artifactId>
  <executions>
    <execution>
      <id>add-graphql-sources</id>
      <phase>generate-sources</phase>
      <goals>
        <goal>add-source</goal>
      </goals>
      <configuration>
        <sources>
          <source>${project.build.directory}/generated-sources/graphql</source>
        </sources>
      </configuration>
    </execution>
  </executions>
</plugin>
```

For a real-world example, see the GTFS GraphQL API in OpenTripPlanner: the `application/pom.xml` and
`application/src/build/gtfs-graphql-codegen.yml` files.

The generated code depends on `graphql-java` (for example `graphql.schema.DataFetcher`), so the
consuming project must have it on its compile classpath.

## Configuration

```yaml
schema: path/to/schema.graphqls # relative to the config file

generates:
  # One entry per generated file, named <className>.java
  MyTypes.java:
    plugins:
      - java # enums, input classes and <Type><Field>Args classes
    config:
      className: MyTypes
      listType: List
  MyDataFetchers.java:
    plugins:
      - java-resolvers # DataFetcher interfaces for object types
    config:
      className: MyDataFetchers

# Shared config; a per-file config overrides these keys
config:
  package: org.example.generated
  typesPrefix: GraphQL
  scalars:
    Duration: java.time.Duration
  mappers:
    Stop: org.example.model.Stop#Stop
```

- `scalars`: maps a GraphQL scalar to a Java type. Unmapped custom scalars become `Object`.
- `mappers`: maps a GraphQL type to a Java type. Use `fully.qualified.Import#TypeExpression` to
  import a class and use a (possibly generic) type expression, for example
  `graphql.relay.Edge#Edge<TransitAlert>`. Unmapped types become `Object`.
- `listType`: the Java type used for GraphQL lists (default `Iterable`).
- `typesPrefix`: a prefix for all generated type names (default none).
- `className`: the name of the generated outer class. It must match the output file name.

## Building

```sh
mvn verify
```

The build formats the code with [Spotless](https://github.com/diffplug/spotless) and Prettier, so
`node` and `npm` must be on the `PATH`. Use `-Dplugin.spotless.goal=check` to only check the
formatting, or `-Dplugin.spotless.skip` to skip it.

## License

[MIT](LICENSE). The design and configuration format are based on
[GraphQL Code Generator](https://github.com/dotansimha/graphql-code-generator) and its
[java plugins](https://github.com/dotansimha/graphql-code-generator-community), which are also
MIT-licensed. See the [LICENSE](LICENSE) file for details.
