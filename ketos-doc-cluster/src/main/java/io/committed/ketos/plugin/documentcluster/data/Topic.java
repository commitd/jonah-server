package io.committed.ketos.plugin.documentcluster.data;

import java.util.List;
import io.committed.ketos.common.data.BaleenDocument;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Topic {
  private double score;
  private String label;
  private List<String> keywords;
  private List<BaleenDocument> documents;

  @GraphQLQuery(name = "count")
  public int getSize() {
    return documents == null ? 0 : documents.size();
  }
}
