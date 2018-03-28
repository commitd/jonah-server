package io.committed.ketos.graphql.baleen.mutations;

import java.util.Optional;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.CrudRelationProvider;
import io.committed.ketos.common.references.BaleenRelationReference;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import reactor.core.publisher.Flux;

/**
 * CRUD mutations for relations.
 */
@GraphQLService
public class RelationCrudMutation
    extends AbstractCrudMutation<BaleenRelationReference, BaleenRelation, CrudRelationProvider> {

  public RelationCrudMutation(final DataProviders dataProviders) {
    super(dataProviders, BaleenRelation.class, CrudRelationProvider.class);
  }

  @GraphQLMutation(name = "deleteRelation", description = "Delete relation by id")
  public Flux<DataProvider> deleteRelation(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "reference") @GraphQLNonNull final BaleenRelationReference id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return delete(Optional.ofNullable(datasetId), id, hints);
  }

  @GraphQLMutation(name = "saveRelation", description = "Save relation by id")
  public Flux<DataProvider> saveRelation(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "relation") @GraphQLNonNull final BaleenRelation item,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return save(Optional.ofNullable(datasetId), item, hints);
  }

}
