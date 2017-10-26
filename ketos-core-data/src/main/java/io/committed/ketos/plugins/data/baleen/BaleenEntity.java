package io.committed.ketos.plugins.data.baleen;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaleenEntity {

  @GraphQLId
  @GraphQLQuery(name = "id", description = "The id for this entity")
  private String id;

  @GraphQLQuery(name = "docId",
      description = "The id of the document that this entity mentions of this entity")
  private String docId;

  @GraphQLQuery(name = "mentions", description = "The mentions of this entity")
  private List<BaleenMention> mentions;

  @GraphQLQuery(name = "values")
  public List<String> getValues() {
    return mentions.stream().map(BaleenMention::getValue).collect(Collectors.toList());
  }

  @GraphQLQuery(name = "longestValue")
  public Optional<String> longestValue(@GraphQLContext final BaleenEntity entity) {
    return entity.getMentions().stream().map(BaleenMention::getValue)
        .collect(Collectors.maxBy((o1, o2) -> Integer.compare(o1.length(), o2.length())));
  }


  @GraphQLQuery(name = "types")
  public List<String> getTypes() {
    return mentions.stream().map(BaleenMention::getType).collect(Collectors.toList());
  }

  @GraphQLQuery(name = "type")
  public Optional<String> getType() {
    return mentions.stream().map(BaleenMention::getType).findFirst();
  }



}
