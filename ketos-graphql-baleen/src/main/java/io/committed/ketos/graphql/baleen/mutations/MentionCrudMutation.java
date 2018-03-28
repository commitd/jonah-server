package io.committed.ketos.graphql.baleen.mutations;

import java.util.Optional;

import reactor.core.publisher.Flux;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.CrudMentionProvider;
import io.committed.ketos.common.references.BaleenMentionReference;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;

/** CRUD mutations for mentions. */
@GraphQLService
public class MentionCrudMutation
    extends AbstractCrudMutation<BaleenMentionReference, BaleenMention, CrudMentionProvider> {

  public MentionCrudMutation(final DataProviders dataProviders) {
    super(dataProviders, BaleenMention.class, CrudMentionProvider.class);
  }

  @GraphQLMutation(name = "deleteMention", description = "Delete mention by id")
  public Flux<DataProvider> deleteMention(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "reference") @GraphQLNonNull final BaleenMentionReference id,
      @GraphQLArgument(
            name = "hints",
            description =
                "Provide hints about the datasource or database which should be used to execute this query"
          )
          final DataHints hints) {

    return delete(Optional.ofNullable(datasetId), id, hints);
  }

  @GraphQLMutation(name = "saveMention", description = "Save mention by id")
  public Flux<DataProvider> saveMention(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "mention") @GraphQLNonNull final BaleenMention item,
      @GraphQLArgument(
            name = "hints",
            description =
                "Provide hints about the datasource or database which should be used to execute this query"
          )
          final DataHints hints) {

    return save(Optional.ofNullable(datasetId), item, hints);
  }
}
