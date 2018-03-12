package io.committed.ketos.graphql.baleen.corpus;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.core.dto.analytic.TermCount;
import io.committed.invest.core.utils.FieldUtils;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionFilter;
import io.committed.ketos.common.graphql.input.MentionProbe;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.common.utils.BinUtils;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@GraphQLService
public class CorpusMentionService extends AbstractGraphQlService {

  @Autowired
  public CorpusMentionService(final DataProviders corpusProviders) {
    super(corpusProviders);
  }

  @GraphQLQuery(name = "mentions", description = "Get mentions")
  public Flux<BaleenMention> getEntities(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "probe", description = "The type of the entity") final MentionProbe probe,
      @GraphQLArgument(name = "offset", defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size", defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    if (probe != null) {
      return getProvidersFromContext(corpus, MentionProvider.class, hints)
          .flatMap(p -> p.getByExample(probe, offset, size))
          .doOnNext(eachAddParent(corpus));
    } else {
      return getProviders(corpus, MentionProvider.class, hints)
          .flatMap(p -> p.getAll(offset, size))
          .doOnNext(eachAddParent(corpus));
    }
  }

  @GraphQLQuery(name = "searchMentions", description = "Search all mentions")
  public MentionSearch searchForEntities(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLNonNull @GraphQLArgument(name = "query",
          description = "The filter to entity") final MentionFilter mentionFilter,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return new MentionSearch(corpus, mentionFilter);
  }

  @GraphQLQuery(name = "mention", description = "Get entities by id")
  public Mono<BaleenMention> getById(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "id") @GraphQLId final String id, @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, MentionProvider.class, hints)
        .flatMap(p -> p.getById(id))
        .next()
        .doOnNext(eachAddParent(corpus));
  }

  @GraphQLQuery(name = "countMentions", description = "Number of mentions in corpus")
  public Mono<Long> countMentions(@GraphQLContext final BaleenCorpus corpus, @GraphQLArgument(
      name = "hints",
      description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {
    return getProviders(corpus, MentionProvider.class, hints)
        .flatMap(MentionProvider::count)
        .reduce(Long::sum);
  }


  @GraphQLQuery(name = "countByMentionField", description = "Count of mention by value")
  public Mono<TermCount> countByField(@GraphQLContext final BaleenCorpus corpus,
      @GraphQLArgument(name = "query",
          description = "Search query") final MentionFilter mentionFilter,
      @GraphQLNonNull @GraphQLArgument(name = "field",
          description = "Provide hints about the datasource or database which should be used to execute this query") final String field,
      @GraphQLArgument(name = "size", defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    final List<String> path = FieldUtils.fieldSplitter(field);

    if (path.isEmpty()) {
      return Mono.empty();
    }

    return BinUtils.joinTermBins(getProviders(corpus, MentionProvider.class, hints)
        .flatMap(p -> p.countByField(Optional.ofNullable(mentionFilter), path, size)));
  }
}

