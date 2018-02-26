package io.committed.ketos.plugins.data.mongo.providers;

import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoCrudDataProvider;
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

  public MongoCrudEntityProvider(final String dataset, final String datasource, final MongoDatabase mongoDatabase,
      final String entityCollection) {
    super(dataset, datasource, mongoDatabase);
    this.entityCollection = entityCollection;
  }

  @Override
  public Mono<Boolean> delete(final BaleenEntityReference reference) {
    // Must
    // - delete actual entity
    // we leave the mentions

    return delete(entityCollection, filterForEntity(reference.getDocumentId(), reference.getEntityId()));
  }

  @Override
  public Mono<Boolean> save(final BaleenEntity item) {
    // NOTE if you have changed the docId here (or entityId) then you'll not be replacing the old one!
    return replace(entityCollection, filterForEntity(item.getDocId(), item.getId()), toOutputEntity(item),
        OutputEntity.class);

  }

  private Bson filterForEntity(final String documentId, final String entityId) {
    return Filters.and(
        Filters.eq(BaleenProperties.EXTERNAL_ID, entityId),
        Filters.eq(BaleenProperties.DOC_ID, documentId));
  }

  private OutputEntity toOutputEntity(final BaleenEntity item) {
    final OutputEntity o = new OutputEntity();
    o.setDocId(item.getDocId());
    o.setExternalId(item.getId());
    // TODO: ... mentions will be lost here. We need to actually get the entity and then put it here!
    o.setProperties(item.getProperties().asMap());
    o.setSubType(item.getSubType());
    o.setType(item.getType());
    o.setValue(item.getValue());
    return o;
  }


}
