package io.committed.ketos.plugin.documentcluster.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.leangen.graphql.annotations.GraphQLQuery;

/** Clusters of topics (dto) */
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
