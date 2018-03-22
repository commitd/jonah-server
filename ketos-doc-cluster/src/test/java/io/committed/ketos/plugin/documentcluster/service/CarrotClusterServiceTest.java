package io.committed.ketos.plugin.documentcluster.service;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import io.committed.ketos.plugin.documentcluster.DocumentFixtures;
import io.committed.ketos.plugin.documentcluster.data.Clusters;
import io.committed.ketos.plugin.documentcluster.data.Topic;

public class CarrotClusterServiceTest {

  @Test
  public void testWithoutHint() {
    final CarrotClusterService service = new CarrotClusterService();

    final Clusters clusters = service.cluster(Optional.empty(), DocumentFixtures.flux()).block();


    assertValidClusters(clusters);
  }


  @Test
  public void testWithHint() {
    final CarrotClusterService service = new CarrotClusterService();

    final Clusters clusters = service.cluster(Optional.of("United States"), DocumentFixtures.flux()).block();

    assertValidClusters(clusters);
  }

  /**
   * Assert valid clusters - we don't know what carrot will do, so just check the output looks right
   *
   * @param clusters the clusters
   */
  private void assertValidClusters(final Clusters clusters) {
    assertThat(clusters).isNotNull();

    assertThat(clusters.getSize()).isGreaterThan(0);

    final List<Topic> topics = clusters.getTopics();
    assertThat(topics.size()).isEqualTo(clusters.getSize());


    topics.stream().forEach(this::assertValidTopic);
  }

  private void assertValidTopic(final Topic topic) {
    assertThat(topic).isNotNull();
    assertThat(topic.getSize()).isGreaterThan(0);
    assertThat(topic.getSize()).isEqualTo(topic.getDocuments().size());

    assertThat(topic.getLabel()).isNotBlank();
    assertThat(topic.getScore()).isGreaterThanOrEqualTo(0);
    assertThat(topic.getKeywords()).isNotEmpty();

    assertThat(topic.getDocuments()).doesNotContainNull();

  }
}
