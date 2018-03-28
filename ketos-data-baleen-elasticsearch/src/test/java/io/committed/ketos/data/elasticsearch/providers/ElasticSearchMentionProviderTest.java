package io.committed.ketos.data.elasticsearch.providers;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.ElasticsearchTestResource;
import io.committed.ketos.data.elasticsearch.factory.EsMentionProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractMentionProviderTest;

public class ElasticSearchMentionProviderTest extends AbstractMentionProviderTest {

  private static final ElasticsearchTestResource resource = new ElasticsearchTestResource();

  @BeforeClass
  public static void beforeClass() {
    resource.setupElastic("documents.json");
  }

  @AfterClass
  public static void afterClass() {
    resource.cleanElastic();
  }

  @Override
  public MentionProvider getMentionProvider() {
    final EsMentionProviderFactory factory = new EsMentionProviderFactory(new ObjectMapper());
    final Map<String, Object> settings = new HashMap<>();
    settings.put(
        AbstractElasticsearchDataProviderFactory.SETTING_INDEX, ElasticsearchTestResource.TEST_DB);
    settings.put(AbstractElasticsearchDataProviderFactory.SETTING_PORT, resource.getPort());
    settings.put(
        AbstractElasticsearchDataProviderFactory.SETTING_CLUSTER, resource.getClusterName());

    return factory
        .build(ElasticsearchTestResource.TEST_DB, ElasticsearchTestResource.TEST_DB, settings)
        .block();
  }
}
