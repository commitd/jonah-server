package io.committed.ketos.plugins.data.mongo.providers;


import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.ketos.common.baleenconsumer.Converters;
import io.committed.ketos.common.baleenconsumer.OutputEntity;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.EntitySearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.plugins.data.mongo.filters.EntityFilters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoEntityProvider extends AbstractBaleenMongoDataProvider<OutputEntity>
    implements EntityProvider {

  @Autowired
  public MongoEntityProvider(final String dataset, final String datasource,
      final MongoDatabase mongoDatabase, final String collectionName) {
    super(dataset, datasource, mongoDatabase, collectionName, OutputEntity.class);
  }

  @Override
  public Mono<BaleenEntity> getById(final String id) {
    return findByExternalId(id).map(Converters::toBaleenEntity);
  }

  @Override
  public Flux<BaleenEntity> getByDocument(final BaleenDocument document) {
    return findByDocumentId(document.getId()).map(Converters::toBaleenEntity);
  }


  @Override
  public Flux<BaleenEntity> getAll(final int offset, final int size) {
    return findAll(offset, size).map(Converters::toBaleenEntity);
  }

  @Override
  public Flux<TermBin> countByField(final Optional<EntityFilter> filter, final List<String> path,
      final int size) {
    return termAggregation(EntityFilters.createFilter(filter, "", false), path, size);
  }

  @Override
  public EntitySearchResult search(final EntitySearch entitySearch, final int offset, final int size) {

    final Optional<Bson> filter = EntityFilters.createFilter(entitySearch);

    final Mono<Long> total;
    final Flux<BaleenEntity> flux;
    if (filter.isPresent()) {
      total = toMono(getCollection().count(filter.get()));
      flux = toFlux(getCollection().find(filter.get()))
          .skip(offset)
          .take(size)
          .map(Converters::toBaleenEntity);
    } else {
      total = count();
      flux = getAll(offset, size);
    }

    return new EntitySearchResult(flux, total);
  }
}
