package io.committed.ketos.plugin.documentcluster.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import java.util.Optional;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.output.DocumentSearch;
import io.committed.ketos.common.graphql.output.Documents;
import io.committed.ketos.plugin.documentcluster.DocumentFixtures;
import io.committed.ketos.plugin.documentcluster.data.Clusters;
import io.committed.ketos.plugin.documentcluster.service.CarrotClusterService;
import reactor.core.publisher.Mono;

public class DocumentClusterResolverTest {

  @Test
  public void testWithDocuments() {
    final Clusters clusters = new Clusters();
    final CarrotClusterService clusterer = mock(CarrotClusterService.class);
    doReturn(Mono.just(clusters)).when(clusterer).cluster(ArgumentMatchers.any(), ArgumentMatchers.any());

    final DocumentClusterResolver resolver = new DocumentClusterResolver(clusterer);

    final Documents dsr =
        new Documents(null, Mono.just(10L), DocumentFixtures.flux(), 0, 10);
    final Mono<Clusters> cluster = resolver.cluster(dsr);

    verify(clusterer).cluster(ArgumentMatchers.any(), ArgumentMatchers.any());
    assertThat(cluster.block()).isEqualTo(clusters);
  }

  @Test
  public void testWithQueryParent() {
    final CarrotClusterService clusterer = mock(CarrotClusterService.class);

    final DocumentClusterResolver resolver = new DocumentClusterResolver(clusterer);

    final DocumentFilter df = new DocumentFilter();
    df.setContent("test");
    final DocumentSearch ds = new DocumentSearch();
    ds.setDocumentFilter(df);
    final Documents dsr =
        new Documents(ds, Mono.just(10L), DocumentFixtures.flux(), 0, 10);
    resolver.cluster(dsr);

    verify(clusterer).cluster(ArgumentMatchers.eq(Optional.of("test")), ArgumentMatchers.any());
  }


  @Test
  public void testWithNull() {
    final CarrotClusterService clusterer = mock(CarrotClusterService.class);
    doReturn(Mono.empty()).when(clusterer).cluster(ArgumentMatchers.any(), ArgumentMatchers.any());

    final DocumentClusterResolver resolver = new DocumentClusterResolver(clusterer);

    final Documents dsr =
        new Documents(null, Mono.just(0L), null, 0, 10);

    assertThat(resolver.cluster(dsr).blockOptional()).isEmpty();
    assertThat(resolver.cluster(null).blockOptional()).isEmpty();
  }
}
