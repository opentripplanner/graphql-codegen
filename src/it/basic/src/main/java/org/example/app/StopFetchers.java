package org.example.app;

import graphql.schema.DataFetcher;
import org.example.generated.AppDataFetchers;
import org.example.generated.AppTypes;

/** Compiles only if the generated sources are added to the source roots. */
public class StopFetchers implements AppDataFetchers.GraphQLStop {

  @Override
  public DataFetcher<String> name() {
    return env -> env.<Stop>getSource().name();
  }

  @Override
  public DataFetcher<Object> mode() {
    return env -> AppTypes.GraphQLMode.BUS;
  }
}
