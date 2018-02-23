package io.committed.ketos.graphql.baleen.mutations;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.invest.support.data.AbstractCrudMutation;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.CrudMentionProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import reactor.core.publisher.Flux;

@GraphQLService
public class MentionCrudMutation extends AbstractCrudMutation<BaleenMention, CrudMentionProvider> {

  public MentionCrudMutation(final DataProviders dataProviders) {
    super(dataProviders, BaleenMention.class, CrudMentionProvider.class);
  }

  @GraphQLMutation(name = "deleteMention", description = "Delete mention by id")
  public Flux<DataProvider> deleteMention(
      @GraphQLArgument(name = "id") @GraphQLNonNull final String id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return delete(id, hints);
  }

  @GraphQLMutation(name = "saveMention", description = "Save mention by id")
  public Flux<DataProvider> saveMention(
      @GraphQLArgument(name = "id") @GraphQLNonNull final BaleenMention item,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return save(item, hints);
  }

}
