package io.committed.ketos.plugin.documentcluster.resolvers;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.graphql.output.Documents;
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

  @GraphQLQuery(name = "cluster")
  public Mono<Clusters> cluster(@GraphQLContext final Documents results) {
    if (results == null || results.getResults() == null) {
      return Mono.empty();
    }

    // Use the parent to find a suitable query to pass in
    final Optional<String> query = results.findParent(DocumentSearch.class)
        .flatMap(search -> {
          if (search.getDocumentFilter() != null) {
            return Optional.ofNullable(search.getDocumentFilter().getContent());
          } else {
            return Optional.empty();
          }
        });

    return cluster.cluster(query, results.getResults());
  }
}
