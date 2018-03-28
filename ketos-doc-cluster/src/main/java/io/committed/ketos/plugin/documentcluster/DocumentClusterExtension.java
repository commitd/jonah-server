package io.committed.ketos.plugin.documentcluster;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.committed.invest.extensions.InvestGraphQlExtension;

/**
 * Extension which add document cluster services to search results.
 *
 */
@Configuration
@Import(DocumentClusterConfig.class)
public class DocumentClusterExtension implements InvestGraphQlExtension {

  @Override
  public String getName() {
    return "Document clustering";
  }

  @Override
  public String getDescription() {
    return "Cluster documents into categories";
  }
}
