package io.committed.ketos.plugins.data.mongo.providers;

import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoCrudDataProvider;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputEntity;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.CrudEntityProvider;
import io.committed.ketos.common.references.BaleenEntityReference;
import reactor.core.publisher.Mono;

public class MongoCrudEntityProvider
    extends AbstractMongoCrudDataProvider<BaleenEntityReference, BaleenEntity>
    implements CrudEntityProvider {

  private final String entityCollection;
  private final String mentionCollection;
  private final String relationCollection;

  public MongoCrudEntityProvider(final String dataset, final String datasource, final MongoDatabase mongoDatabase,
      final String mentionCollection, final String entityCollection, final String relationCollection) {
    super(dataset, datasource, mongoDatabase);
    this.mentionCollection = mentionCollection;
    this.entityCollection = entityCollection;
    this.relationCollection = relationCollection;
  }

  @Override
  public Mono<Boolean> delete(final BaleenEntityReference reference) {

    delete(mentionCollection, Filters.and(
        Filters.eq(BaleenProperties.ENTITY_ID, reference.getEntityId()),
        Filters.eq(BaleenProperties.DOC_ID, reference.getDocumentId())));

    delete(relationCollection, Filters.and(
        Filters.eq(BaleenProperties.DOC_ID, reference.getDocumentId()),
        Filters.or(
            Filters.eq(BaleenProperties.RELATION_SOURCE + "." + BaleenProperties.ENTITY_ID, reference.getEntityId()),
            Filters.eq(BaleenProperties.RELATION_TARGET + "." + BaleenProperties.ENTITY_ID,
                reference.getEntityId()))));

    return delete(entityCollection, filterForEntity(reference.getDocumentId(), reference.getEntityId()));
  }

  @Override
  public Mono<Boolean> save(final BaleenEntity item) {
    // NOTE if you have changed the docId here (or entityId) then you'll not be replacing the old one!
    return replace(entityCollection, filterForEntity(item.getDocId(), item.getId()), Converters.toOutputEntity(item),
        OutputEntity.class);

  }

  private Bson filterForEntity(final String documentId, final String entityId) {
    return Filters.and(
        Filters.eq(BaleenProperties.EXTERNAL_ID, entityId),
        Filters.eq(BaleenProperties.DOC_ID, documentId));
  }

}
