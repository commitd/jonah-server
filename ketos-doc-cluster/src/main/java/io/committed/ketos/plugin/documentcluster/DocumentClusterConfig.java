package io.committed.ketos.plugin.documentcluster;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.committed.ketos.plugin.documentcluster.resolvers.DocumentClusterResolver;
import io.committed.ketos.plugin.documentcluster.service.CarrotClusterService;

/**
 * Configuration wiring for document cluster services
 *
 */
@Configuration
public class DocumentClusterConfig {

  @Bean
  public CarrotClusterService carrotClusterer() {
    return new CarrotClusterService();
  }

  @Bean
  public DocumentClusterResolver documentClusterResolver(final CarrotClusterService clusterer) {
    return new DocumentClusterResolver(clusterer);
  }

}
