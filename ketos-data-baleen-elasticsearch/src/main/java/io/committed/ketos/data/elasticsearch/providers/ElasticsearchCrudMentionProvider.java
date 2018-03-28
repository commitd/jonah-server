package io.committed.ketos.data.elasticsearch.providers;

import java.util.Optional;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.CrudMentionProvider;
import io.committed.ketos.common.references.BaleenMentionReference;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import io.committed.ketos.data.elasticsearch.repository.EsRelationService;

/**
 * Elasticsearch CrudMentionProvider.
 *
 */
public class ElasticsearchCrudMentionProvider
    extends AbstractElasticsearchCrudDataProvider<BaleenMentionReference, BaleenMention>
    implements CrudMentionProvider {

  private final EsMentionService mentions;
  private final EsRelationService relations;
  private final String documentType;

  public ElasticsearchCrudMentionProvider(final String dataset, final String datasource,
      final String documentType,
      final EsMentionService mentions, final EsRelationService relations) {
    super(dataset, datasource);
    this.documentType = documentType;
    this.mentions = mentions;
    this.relations = relations;
  }

  @Override
  public boolean delete(final BaleenMentionReference reference) {

    // Remove mentionId from any entities which contain it entities..
    // but we don't map it here in Ketos so just leaving it for now

    delete(relations, reference.getDocumentId(), BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.EXTERNAL_ID,
        reference.getMentionId());
    delete(relations, reference.getDocumentId(), BaleenProperties.RELATION_TARGET + "." + BaleenProperties.EXTERNAL_ID,
        reference.getMentionId());

    return delete(mentions, reference.getDocumentId(), reference.getMentionId());
  }



  @Override
  public boolean save(final BaleenMention item) {
    final OutputMention mention = Converters.toOutputMention(item);

    relations.updateSource(item.getId(), mention);
    relations.updateTarget(item.getId(), mention);

    return mentions.updateOrSave(Optional.of(documentType), Optional.ofNullable(item.getDocId()),
        BaleenProperties.EXTERNAL_ID, item.getId(),
        mention);
  }

}
