package io.committed.ketos.graphql.baleen.mutations;

import java.util.Optional;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.providers.baleen.CrudEntityProvider;
import io.committed.ketos.common.references.BaleenEntityReference;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import reactor.core.publisher.Flux;

@GraphQLService
public class EntityCrudMutation extends AbstractCrudMutation<BaleenEntityReference, BaleenEntity, CrudEntityProvider> {

  public EntityCrudMutation(final DataProviders dataProviders) {
    super(dataProviders, BaleenEntity.class, CrudEntityProvider.class);
  }

  @GraphQLMutation(name = "deleteEntity", description = "Delete entity by id")
  public Flux<DataProvider> deleteEntity(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "reference") @GraphQLNonNull final BaleenEntityReference id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return delete(Optional.ofNullable(datasetId), id, hints);
  }

  @GraphQLMutation(name = "saveEntity", description = "Save entity by id")
  public Flux<DataProvider> saveEntity(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "entity") @GraphQLNonNull final BaleenEntity item,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return save(Optional.ofNullable(datasetId), item, hints);
  }

}
