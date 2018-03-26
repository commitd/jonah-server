package io.committed.ketos.graphql.baleen.entity;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;

@GraphQLService
public class EntityMentionsService extends AbstractGraphQlService {

  @Autowired
  public EntityMentionsService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "mentions", description = "The mentions of this entity")
  public Flux<BaleenMention> getMentionsFromEntity(@GraphQLContext final BaleenEntity entity,
      @GraphQLArgument(name = "size", defaultValue = "1000") final int size,
      @GraphQLArgument(
          name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final MentionFilter filter = new MentionFilter();
    filter.setDocId(entity.getDocId());
    filter.setEntityId(entity.getId());

    final MentionSearch search = new MentionSearch(entity, filter);
    return getProvidersFromContext(entity, MentionProvider.class, hints)
        .flatMap(p -> p.search(search, 0, size).getResults())
        .doOnNext(eachAddParent(entity));

  }

}
