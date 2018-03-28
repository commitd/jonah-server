package io.committed.ketos.plugins.data.mongo.providers;

import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.support.data.mongo.AbstractMongoCrudDataProvider;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.CrudDocumentProvider;
import io.committed.ketos.common.references.BaleenDocumentReference;

/**
 * Mongo CrudDocumentProvider.
 *
 */
public class MongoCrudDocumentProvider
    extends AbstractMongoCrudDataProvider<BaleenDocumentReference, BaleenDocument>
    implements CrudDocumentProvider {


  private final String documentCollection;
  private final String mentionCollection;
  private final String entityCollection;
  private final String relationCollection;

  public MongoCrudDocumentProvider(final String dataset, final String datasource, final MongoDatabase mongoDatabase,
      final String documentCollection, final String mentionCollection, final String entityCollection,
      final String relationCollection) {
    super(dataset, datasource, mongoDatabase);
    this.documentCollection = documentCollection;
    this.mentionCollection = mentionCollection;
    this.entityCollection = entityCollection;
    this.relationCollection = relationCollection;
  }

  @Override
  public boolean delete(final BaleenDocumentReference reference) {
    // Delete everything

    final Bson filter = Filters.eq(BaleenProperties.DOC_ID, reference.getDocumentId());

    delete(relationCollection, filter);
    delete(mentionCollection, filter);
    delete(entityCollection, filter);

    return delete(documentCollection, filterForDoc(reference.getDocumentId()));
  }

  @Override
  public boolean save(final BaleenDocument item) {
    return replace(documentCollection, filterForDoc(item.getId()), Converters.toOutputDocument(item),
        OutputDocument.class);
  }


  private Bson filterForDoc(final String id) {
    return Filters.eq(BaleenProperties.EXTERNAL_ID, id);
  }
}
