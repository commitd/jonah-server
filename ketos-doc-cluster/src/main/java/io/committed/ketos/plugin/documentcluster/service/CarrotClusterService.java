package io.committed.ketos.plugin.documentcluster.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Controller;
import org.carrot2.core.ControllerFactory;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.core.ProcessingResult;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.plugin.documentcluster.data.Clusters;
import io.committed.ketos.plugin.documentcluster.data.Topic;

/**
 * Use Carrot2 to produce a document cluster.
 *
 * <p>This implementation is based on the ClusteringDocumentList example.
 */
@Service
public class CarrotClusterService {

  /**
   * Cluster the documents.
   *
   * @param query the query (which can be used by Carrot to tailor result)
   * @param documents the documents
   * @return the clusters
   */
  public Mono<Clusters> cluster(
      final Optional<String> query, final Flux<BaleenDocument> documents) {

    final Map<String, BaleenDocument> idToBaleenDocument =
        documents.collectMap(BaleenDocument::getId).block();

    return convertToCarrotDocuments(idToBaleenDocument.values())
        .map(l -> clusterWithLingo(query, l))
        .map(l -> convertToClusters(l, idToBaleenDocument));
  }

  protected Mono<List<Document>> convertToCarrotDocuments(
      final Collection<BaleenDocument> documents) {

    return Flux.fromIterable(documents)
        .map(
            d -> {
              final String title = d.getProperties().get("title", "");
              final String summary = d.getContent();
              final String contentUrl = null;
              final LanguageCode language = null;
              final String id = d.getId();
              return new Document(title, summary, contentUrl, language, id);
            })
        .collectList();
  }

  protected List<Cluster> clusterWithLingo(
      final Optional<String> query, final List<Document> input) {
    /* A controller to manage the processing pipeline. */

    try (final Controller controller = ControllerFactory.createSimple()) {

      final ProcessingResult result =
          controller.process(input, query.orElse(null), LingoClusteringAlgorithm.class);
      return result.getClusters();
    }
  }

  protected Clusters convertToClusters(
      final List<Cluster> clustered, final Map<String, BaleenDocument> idToBaleenDocument) {
    final List<Topic> topics =
        clustered
            .stream()
            .map(t -> convertToTopic(t, idToBaleenDocument))
            .collect(Collectors.toList());
    return new Clusters(topics);
  }

  protected Topic convertToTopic(
      final Cluster cluster, final Map<String, BaleenDocument> idToBaleenDocument) {

    final Topic topic = new Topic();

    topic.setScore(cluster.getScore());
    topic.setLabel(cluster.getLabel());
    topic.setKeywords(cluster.getPhrases());

    final List<BaleenDocument> baleenDocuments =
        cluster
            .getDocuments()
            .stream()
            .map(d -> idToBaleenDocument.get(d.getStringId()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    topic.setDocuments(baleenDocuments);
    return topic;
  }
}
