package io.committed.ketos.plugins.data.mongo.providers;

import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoCrudDataProvider;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.CrudMentionProvider;
import io.committed.ketos.common.references.BaleenMentionReference;

/**
 * Mongo CrudMentionProvider.
 *
 */
public class MongoCrudMentionProvider
    extends AbstractMongoCrudDataProvider<BaleenMentionReference, BaleenMention>
    implements CrudMentionProvider {

  private final String mentionCollection;
  private final String entityCollection;
  private final String relationCollection;

  public MongoCrudMentionProvider(final String dataset, final String datasource, final MongoDatabase mongoDatabase,
      final String mentionCollection, final String entityCollection, final String relationCollection) {
    super(dataset, datasource, mongoDatabase);
    this.mentionCollection = mentionCollection;
    this.entityCollection = entityCollection;
    this.relationCollection = relationCollection;
  }

  @Override
  public boolean delete(final BaleenMentionReference reference) {
    // Must:
    // - delete the mention
    // - update the entityId so it doesn't have delete the mentionId from the entity
    // - delete any relation which has this mention


    update(entityCollection, Filters.and(
        Filters.in(BaleenProperties.MENTION_IDS, reference.getMentionId()),
        Filters.eq(BaleenProperties.DOC_ID, reference.getDocumentId())),
        Updates.pull(BaleenProperties.MENTION_IDS, reference.getMentionId()));

    delete(relationCollection, Filters.and(
        Filters.eq(BaleenProperties.DOC_ID, reference.getDocumentId()),
        Filters.or(
            Filters.eq(BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.EXTERNAL_ID, reference.getMentionId()),
            Filters.eq(BaleenProperties.RELATION_TARGET + "." + BaleenProperties.EXTERNAL_ID,
                reference.getMentionId()))));

    return delete(mentionCollection, filterForMention(reference.getDocumentId(), reference.getMentionId()));
  }

  private Bson filterForMention(final String documentId, final String mentiondId) {
    return Filters.and(
        Filters.eq(BaleenProperties.EXTERNAL_ID, mentiondId),
        Filters.eq(BaleenProperties.DOC_ID, documentId));
  }

  @Override
  public boolean save(final BaleenMention item) {
    // Must
    // - update the actual mention
    // - update any relation which has this mention as either source or target
    // NOTE We know (that baleen) derives the entity from the mention. But we can't change the entity
    // really that up to the caller to also set.

    final OutputMention outputMention = Converters.toOutputMention(item);

    // Update relation source

    final Bson sourceFilter = Filters.and(
        Filters.eq(BaleenProperties.DOC_ID, item.getDocId()),
        Filters.eq(BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.EXTERNAL_ID, item.getId()));
    final Bson sourceUpdate = Updates.set(BaleenProperties.RELATION_SOURCE, outputMention);
    update(relationCollection, sourceFilter, sourceUpdate);


    final Bson targetFilter = Filters.and(
        Filters.eq(BaleenProperties.DOC_ID, item.getDocId()),
        Filters.eq(BaleenProperties.RELATION_TARGET + "." + BaleenProperties.EXTERNAL_ID, item.getId()));
    final Bson targetUpdate = Updates.set(BaleenProperties.RELATION_SOURCE, outputMention);
    update(relationCollection, targetFilter, targetUpdate);

    return replace(mentionCollection, filterForMention(item.getDocId(), item.getId()), outputMention,
        OutputMention.class);
  }


}
