package io.committed.ketos.common.data;

import java.util.Collection;
import java.util.Date;
import io.committed.ketos.common.graphql.support.AbstractGraphQLNode;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class BaleenDocumentInfo extends AbstractGraphQLNode {
  private final String title;
  private final Date date;
  private final String type;
  private final String source;
  private final String language;
  private final Date timestamp;
  private final String classification;
  private final Collection<String> caveats;
  private final Collection<String> releasability;
  private final Collection<String> publishedIds;

}
