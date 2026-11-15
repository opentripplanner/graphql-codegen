package org.opentripplanner.graphql.codegen;

import graphql.GraphQLException;
import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tools.jackson.core.JacksonException;

/**
 * Generates Java types and data fetcher interfaces from a GraphQL schema, and adds the output
 * directory to the compile source roots of the project.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class GenerateMojo extends AbstractMojo {

  /** The codegen configuration file (YAML). The schema path in it is relative to this file. */
  @Parameter(property = "graphql-codegen.configFile", required = true)
  private File configFile;

  /** The source root the Java files are generated into. */
  @Parameter(
    property = "graphql-codegen.outputDirectory",
    defaultValue = "${project.build.directory}/generated-sources/graphql",
    required = true
  )
  private File outputDirectory;

  /** Skips the code generation. */
  @Parameter(property = "graphql-codegen.skip", defaultValue = "false")
  private boolean skip;

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Skipping GraphQL code generation");
      return;
    }
    if (!configFile.isFile()) {
      throw new MojoFailureException("Config file not found: " + configFile);
    }
    try {
      GraphQLCodegen.generate(configFile.toPath(), outputDirectory.toPath());
    } catch (IllegalArgumentException | GraphQLException | JacksonException e) {
      throw new MojoFailureException(e.getMessage(), e);
    } catch (IOException e) {
      throw new MojoExecutionException("GraphQL code generation failed: " + e.getMessage(), e);
    }
    project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
  }
}
