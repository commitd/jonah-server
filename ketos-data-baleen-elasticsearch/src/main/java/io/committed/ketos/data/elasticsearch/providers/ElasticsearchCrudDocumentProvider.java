package io.committed.ketos.data.elasticsearch.providers;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.CrudDocumentProvider;
import io.committed.ketos.common.references.BaleenDocumentReference;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import io.committed.ketos.data.elasticsearch.repository.EsEntityService;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;
import reactor.core.publisher.Mono;

public class ElasticsearchCrudDocumentProvider
    extends AbstractElasticsearchCrudDataProvider<BaleenDocumentReference, BaleenDocument>
    implements CrudDocumentProvider {

  private final EsDocumentService documents;
  private final EsMentionService mentions;
  private final EsRelationService relations;
  private final EsEntityService entities;

  public ElasticsearchCrudDocumentProvider(final String dataset, final String datasource,
      final EsDocumentService documents, final EsMentionService mentions, final EsEntityService entities,
      final EsRelationService relations) {
    super(dataset, datasource);
    this.documents = documents;
    this.mentions = mentions;
    this.entities = entities;
    this.relations = relations;
  }

  @Override
  public Mono<Boolean> delete(final BaleenDocumentReference reference) {

    final MatchQueryBuilder query = QueryBuilders.matchQuery(BaleenProperties.DOC_ID, reference.getDocumentId());

    mentions.delete(query);
    entities.delete(query);
    relations.delete(query);
    final boolean result =
        documents.delete(QueryBuilders.matchQuery(BaleenProperties.EXTERNAL_ID, reference.getDocumentId()));

    return Mono.just(result);
  }

  @Override
  public Mono<Boolean> save(final BaleenDocument item) {
    // TODO Auto-generated method stub
    return null;
  }

}
