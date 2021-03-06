package io.committed.ketos.data.elasticsearch.providers;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.references.BaleenRelationReference;
import io.committed.ketos.data.elasticsearch.ElasticsearchTestResource;
import io.committed.ketos.data.elasticsearch.factory.EsCrudRelationProviderFactory;
import io.committed.ketos.test.common.providers.baleen.AbstractCrudRelationProviderTest;

public class ElasticSearchCrudRelationProviderTest extends AbstractCrudRelationProviderTest {

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
  public CrudDataProvider<BaleenRelationReference, BaleenRelation> getDataProvider() {
    final EsCrudRelationProviderFactory factory =
        new EsCrudRelationProviderFactory(new ObjectMapper());
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

  @Override
  public void testSave() {
    // TODO Embedded Elasticsearch integration tests do not support has child operations
  }
}
