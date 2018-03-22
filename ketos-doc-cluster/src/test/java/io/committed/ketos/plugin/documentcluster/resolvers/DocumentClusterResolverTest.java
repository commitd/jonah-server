package io.committed.ketos.plugin.documentcluster.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.graphql.intermediate.DocumentSearchResult;
import io.committed.ketos.plugin.documentcluster.Documents;
import io.committed.ketos.plugin.documentcluster.data.Clusters;
import io.committed.ketos.plugin.documentcluster.service.CarrotClusterService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DocumentClusterResolverTest {

  @Test
  public void testWithDocuments() {
    final Clusters clusters = new Clusters();
    final CarrotClusterService clusterer = mock(CarrotClusterService.class);
    doReturn(Mono.just(clusters)).when(clusterer).cluster(ArgumentMatchers.any(), ArgumentMatchers.any());

    final DocumentClusterResolver resolver = new DocumentClusterResolver(clusterer);

    final Flux<BaleenDocument> flux = Documents.flux();
    final DocumentSearchResult dsr = DocumentSearchResult.builder()
        .results(flux)
        .build();

    final Mono<Clusters> cluster = resolver.cluster(dsr);

    verify(clusterer).cluster(ArgumentMatchers.any(), ArgumentMatchers.eq(flux));
    assertThat(cluster.block()).isEqualTo(clusters);
  }


  @Test
  public void testWithNull() {
    final CarrotClusterService clusterer = mock(CarrotClusterService.class);

    final DocumentClusterResolver resolver = new DocumentClusterResolver(clusterer);

    final DocumentSearchResult dsr = DocumentSearchResult.builder()
        .build();

    assertThat(resolver.cluster(dsr).blockOptional()).isEmpty();
    assertThat(resolver.cluster(null).blockOptional()).isEmpty();
  }
}
