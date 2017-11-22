package io.committed.ketos.common.data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.committed.ketos.common.graphql.support.AbstractGraphQLNodeSupport;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class BaleenEntity extends AbstractGraphQLNodeSupport<BaleenEntity> {

  @GraphQLId
  @GraphQLQuery(name = "id", description = "The id for this entity")
  private String id;

  @GraphQLQuery(name = "docId",
      description = "The id of the document that this entity mentions of this entity")
  private String docId;

  @GraphQLQuery(name = "mentions", description = "The mentions of this entity")
  private List<BaleenMention> mentions;

  @GraphQLQuery(name = "values", description = "Values associated with this entity")
  public List<String> getValues() {
    return mentions.stream().map(BaleenMention::getValue).collect(Collectors.toList());
  }

  @GraphQLQuery(name = "longestValue", description = "Longest value associated with this entity")
  public Optional<String> longestValue(@GraphQLContext final BaleenEntity entity) {
    return entity.getMentions().stream().map(BaleenMention::getValue)
        .collect(Collectors.maxBy((o1, o2) -> Integer.compare(o1.length(), o2.length())));
  }

  @GraphQLQuery(name = "types",
      description = "Distinct entity types assocated with mentions of this entity")
  public List<String> getTypes() {
    return mentions.stream().map(BaleenMention::getType).distinct().collect(Collectors.toList());
  }

  @GraphQLQuery(name = "type", description = "Primary types associated with this entity")
  public Optional<String> getType() {
    return mentions.stream().map(BaleenMention::getType).findFirst();
  }



}
