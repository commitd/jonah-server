package io.committed.ketos.common.data;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class BaleenEntity extends AbstractGraphQLNode {

  @GraphQLId
  @GraphQLQuery(name = "id", description = "The id for this entity")
  private final String id;

  @GraphQLQuery(name = "docId",
      description = "The id of the document that this entity mentions of this entity")
  private final String docId;

  @GraphQLQuery(name = "mentions", description = "The mentions of this entity")
  private final Flux<BaleenMention> mentions;


  public BaleenEntity(final String id, final String docId, final Flux<BaleenMention> mentions) {
    this.id = id;
    this.docId = docId;
    this.mentions = mentions.doOnNext(m -> m.setParent(this)).cache();

  }

  @GraphQLQuery(name = "values", description = "Values associated with this entity")
  public Flux<String> getValues() {
    return mentions.map(BaleenMention::getValue);
  }

  @GraphQLQuery(name = "longestValue", description = "Longest value associated with this entity")
  public Mono<String> longestValue(@GraphQLContext final BaleenEntity entity) {
    return entity.getMentions()
        .map(BaleenMention::getValue)
        .reduce("", (a, b) -> a.length() > b.length() ? a : b);
  }

  @GraphQLQuery(name = "types",
      description = "Distinct entity types assocated with mentions of this entity")
  public Flux<String> getTypes() {
    return mentions.map(BaleenMention::getType).distinct();
  }

  @GraphQLQuery(name = "type", description = "Primary types associated with this entity")
  public Mono<String> getType() {
    // Whilst this could find the most popular or some other metric its rather meaningless to guess here
    // what is best for the caller.
    return getTypes().next();
  }

}
