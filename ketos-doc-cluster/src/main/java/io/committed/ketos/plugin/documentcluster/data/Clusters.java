package io.committed.ketos.plugin.documentcluster.data;

import java.util.List;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Clusters {
  private List<Topic> topics;

  @GraphQLQuery(name = "count")
  public int getSize() {
    return topics == null ? 0 : topics.size();
  }
}
