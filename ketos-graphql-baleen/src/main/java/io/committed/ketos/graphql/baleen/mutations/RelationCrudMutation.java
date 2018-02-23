package io.committed.ketos.graphql.baleen.mutations;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.invest.support.data.AbstractCrudMutation;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.CrudRelationProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import reactor.core.publisher.Flux;

@GraphQLService
public class RelationCrudMutation extends AbstractCrudMutation<BaleenRelation, CrudRelationProvider> {

  public RelationCrudMutation(final DataProviders dataProviders) {
    super(dataProviders, BaleenRelation.class, CrudRelationProvider.class);
  }

  @GraphQLMutation(name = "deleteRelation", description = "Delete relation by id")
  public Flux<DataProvider> deleteRelation(
      @GraphQLArgument(name = "id") @GraphQLNonNull final String id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return delete(id, hints);
  }

  @GraphQLMutation(name = "saveRelation", description = "Save relation by id")
  public Flux<DataProvider> saveRelation(
      @GraphQLArgument(name = "id") @GraphQLNonNull final BaleenRelation item,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return save(item, hints);
  }

}
