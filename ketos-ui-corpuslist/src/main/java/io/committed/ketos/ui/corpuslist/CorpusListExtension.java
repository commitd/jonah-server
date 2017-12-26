package io.committed.ketos.ui.corpuslist;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.invest.extensions.InvestUiExtension;

/**
 * Extension point for CorpusList.
 *
 */
@Configuration
@ComponentScan
public class CorpusListExtension implements InvestUiExtension {

  @Override
  public String getId() {
    return "CorpusList";
  }

  @Override
  public String getName() {
    return "Corpus List";
  }

  @Override
  public String getDescription() {
    return "List available corpora";
  }

  @Override
  public String getIcon() {
    return "book";
  }

  @Override
  public Class<?> getSettings() {
    return CorpusListSettings.class;
  }

  @Override
  public String getStaticResourcePath() {
    // Do not change this without also changing the pom.xml copy-resources
    // as Maven will copy the output from the JS build into this location
    return "/ui/CorpusList/";
  }
}
