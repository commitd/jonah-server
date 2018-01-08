package io.committed.ketos.data.elasticsearch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {BaleenElasticsearchConfig.class})
// Note this plugin does not use the SpringDataElasticsearch Repository approach
// we have found this to be too restrictive in the past and generally less clean than
// writing similar queries in the ElasticserachTemplate.
// However due to the need for configurability we can't use even Elasticsearchtemplate
// so we drop to the equally expressive ElasticsearchClient.
public class BaleenElasticsearchConfig {

}
