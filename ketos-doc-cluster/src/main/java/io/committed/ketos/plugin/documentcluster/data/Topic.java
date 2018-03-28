package io.committed.ketos.plugin.documentcluster.data;

import java.util.List;

import lombok.Data;

import io.committed.ketos.common.data.BaleenDocument;
import io.leangen.graphql.annotations.GraphQLQuery;

/**
 * Single topics
 *
 * <p>Label and documentation should be non-null. Other fields might vary between providers.
 */
@Data
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
