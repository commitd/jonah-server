package io.committed.ketos.plugins.data.configurer.mongo;

import java.util.Map;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.repository.support.ReactiveMongoRepositoryFactory;
import org.springframework.data.repository.core.support.ReactiveRepositoryFactorySupport;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import io.committed.vessel.server.data.providers.AbstractDataProviderFactory;
import io.committed.vessel.server.data.providers.DataProvider;
import io.committed.vessel.server.data.providers.DatasourceConstants;

public abstract class AbstractMongoDataProviderFactory<P extends DataProvider>
    extends AbstractDataProviderFactory<P> {

  protected AbstractMongoDataProviderFactory(final String id, final Class<P> clazz) {
    super(id, clazz, DatasourceConstants.MONGO);
  }

  protected ReactiveRepositoryFactorySupport buildRepositoryFactory(
      final Map<String, Object> settings) {
    final String connectionString =
        (String) settings.getOrDefault("uri", "mongodb://localhost:27017/");
    final String databaseName = (String) settings.getOrDefault("db", "baleen");


    final MongoClient mongoClient = MongoClients.create(connectionString);
    final SimpleReactiveMongoDatabaseFactory mongoDatabaseFactory =
        new SimpleReactiveMongoDatabaseFactory(mongoClient, databaseName);

    // If we need to control how the database collections are mapped, so we override the default
    // type mapping.

    // This is largely based off the SimpleReactiveMongoDatabaseFactory.getDefaultMongoConverter
    //
    // final MongoCustomConversions conversions = new
    // MongoCustomConversions(Collections.emptyList());
    //
    // final MongoMappingContext context = new MongoMappingContext();
    // TODO: Here I think we'd have to override createPersistanceEntity to return our own
    // BasicMongoPersistentEntity which woudl override the getCollectionName()
    // to be whatever we want it to be for a specific entity type...
    // context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
    // context.afterPropertiesSet();
    //
    // final MappingMongoConverter converter =
    // new MappingMongoConverter(new NoOpDbRefResolver(), context);
    // converter.setCustomConversions(conversions);
    // converter.afterPropertiesSet();

    // Then use = new ReactiveMongoTemplate(mongoDatabaseFactory, converter) below


    // Finally create the factory support
    final ReactiveMongoTemplate mongoOperations =
        new ReactiveMongoTemplate(mongoDatabaseFactory);
    final ReactiveRepositoryFactorySupport support =
        new ReactiveMongoRepositoryFactory(mongoOperations);


    return support;
  }



}
