package io.committed.ketos.plugin.documentcluster.resolvers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.plugin.documentcluster.data.Clusters;
import io.committed.ketos.plugin.documentcluster.service.CarrotClusterService;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Mono;

@GraphQLService
public class DocumentClusterResolver {

  private final CarrotClusterService cluster;

  @Autowired
  public DocumentClusterResolver(final CarrotClusterService cluster) {
    this.cluster = cluster;
  }

  @GraphQLQuery
  public Mono<Clusters> cluster(@GraphQLContext final DocumentSearchResult results) {
    if (results == null || results.getResults() == null) {
      return Mono.empty();
    }

    // TODO DocumentSearch does not have parent, but otherwise we could get the query and pass it over

    return cluster.cluster(Optional.empty(), results.getResults());
  }
}
