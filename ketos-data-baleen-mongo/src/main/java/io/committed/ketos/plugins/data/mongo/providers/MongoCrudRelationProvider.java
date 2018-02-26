package io.committed.ketos.plugins.data.mongo.providers;

import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoCrudDataProvider;
import io.committed.ketos.common.baleenconsumer.OutputRelation;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.CrudRelationProvider;
import io.committed.ketos.common.references.BaleenRelationReference;
import reactor.core.publisher.Mono;

public class MongoCrudRelationProvider
    extends AbstractMongoCrudDataProvider<BaleenRelationReference, BaleenRelation>
    implements CrudRelationProvider {


  private String relationCollection;

  public MongoCrudRelationProvider(final String dataset, final String datasource, final MongoDatabase mongoDatabase,
      final String relationCollection) {
    super(dataset, datasource, mongoDatabase);
    this.relationCollection = relationCollection;
  }

  @Override
  public Mono<Boolean> delete(final BaleenRelationReference reference) {
    // Must
    // - delete actual relation
    // we leave the mentions

    return delete(relationCollection, filterForRelation(reference.getDocumentId(), reference.getRelationId()));
  }

  @Override
  public Mono<Boolean> save(final BaleenRelation item) {
    // NOTE if you have changed the docId here (or entityId) then you'll not be replacing the old one!
    return replace(relationCollection, filterForRelation(item.getDocId(), item.getId()), toOutputRelation(item),
        OutputRelation.class);

  }

  private Bson filterForRelation(final String documentId, final String relationId) {
    return Filters.and(
        Filters.eq(BaleenProperties.EXTERNAL_ID, relationId),
        Filters.eq(BaleenProperties.DOC_ID, documentId));
  }

  private OutputRelation toOutputRelation(final BaleenRelation item) {
    final OutputRelation o = new OutputRelation();
    o.setDocId(item.getDocId());
    o.setExternalId(item.getId());
    o.setProperties(item.getProperties().asMap());
    o.setSubType(item.getSubType());
    o.setType(item.getType());
    o.setValue(item.getValue());
    o.setBegin(item.getBegin());
    o.setEnd(item.getEnd());
    o.setSource(MongoCrudMentionProvider.toOutputMention(item.getSource()));
    o.setTarget(MongoCrudMentionProvider.toOutputMention(item.getTarget()));
    return o;
  }


}
