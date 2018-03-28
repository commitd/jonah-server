package io.committed.ketos.data.elasticsearch;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.committed.invest.extensions.InvestDataExtension;

/**
 * Extension which provides data (providers and factories) for Baleen data providers which are
 * backed by Elasticsearch.
 *
 * Note this plugin does not use the SpringDataElasticsearch Repository approach we have found this
 * to be too restrictive in the past and generally less clean than writing similar queries in the
 * ElasticserachTemplate. However due to the need for configurability we can't use even
 * ElasticsearchTemplate so we drop to the equally expressive ElasticsearchClient.
 *
 */
@Configuration
@Import(BaleenElasticsearchConfig.class)
public class BaleenElasticsearchExtension implements InvestDataExtension {


}
