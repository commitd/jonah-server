package io.committed.ketos.common.data;

import java.util.Collection;
import java.util.Date;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * GraphQL/DTO representation of a Baleen document info (documentation annotation)
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaleenDocumentInfo extends AbstractGraphQLNode {
  private String title;
  private Date date;
  private String type;
  private String source;
  private String language;
  private Date timestamp;
  private String classification;
  private Collection<String> caveats;
  private Collection<String> releasability;
  private Collection<String> publishedIds;

}
