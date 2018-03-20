package io.committed.ketos.graphql.baleen.document;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusDocumentsService;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentInfoServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusDocumentsService corpusDocumentService(DataProviders providers) {
      return new CorpusDocumentsService(providers);
    }

    @Bean
    public DocumentInfoService documentEntityService() {
      return new DocumentInfoService();
    }

    @Bean
    public DocumentProvider dataProvider() {
      DocumentProvider mock = Mockito.mock(DocumentProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }
  }

  @Autowired
  private DocumentProvider documentProvider;

  @Test
  public void testGetInfoNoMetadata() {
    BaleenDocument doc = new BaleenDocument("testDoc", null, "", null);
    when(documentProvider.getById(eq("testDoc"))).thenReturn(Mono.just(doc));
    postQuery(
        corpusQuery("document(id: \"testDoc\") { info { classification date language source timestamp title type } }"),
        defaultVariables())
            .jsonPath("$.data.corpus.document.info.classification").isEqualTo(null)
            .jsonPath("$.data.corpus.document.info.date").isEqualTo(null)
            .jsonPath("$.data.corpus.document.info.language").isEqualTo("NA")
            .jsonPath("$.data.corpus.document.info.source").isEqualTo("")
            .jsonPath("$.data.corpus.document.info.timestamp").isEqualTo(null)
            .jsonPath("$.data.corpus.document.info.title").isEqualTo("Unknown")
            .jsonPath("$.data.corpus.document.info.type").isEqualTo("Unknown");
  }

  @Test
  public void testGetInfo() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(BaleenProperties.DOCUMENT_TITLE, "testTitle");
    BaleenDocument doc = new BaleenDocument("testDoc", null, "", new PropertiesMap(properties));
    when(documentProvider.getById(eq("testDoc"))).thenReturn(Mono.just(doc));
    postQuery(
        corpusQuery("document(id: \"testDoc\") { info { classification date language source timestamp title type } }"),
        defaultVariables())
            .jsonPath("$.data.corpus.document.info.title").isEqualTo("testTitle");
  }

  @Test
  public void testGetInfoPublishedIdsAsList() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(BaleenProperties.PUBLISHED_IDS, Collections.singletonList("pubId"));
    BaleenDocument doc = new BaleenDocument("testDoc", null, "", new PropertiesMap(properties));
    assertPublishedIds(doc);
  }

  @Test
  public void testGetInfoPublishedIdsAsMap() {
    Map<String, Object> properties = new HashMap();
    Map<String, Object> mapWithId = new HashMap<>();
    mapWithId.put("id", "pubId");
    properties.put(BaleenProperties.PUBLISHED_IDS, Collections.singletonList(mapWithId));
    BaleenDocument doc = new BaleenDocument("testDoc", null, "", new PropertiesMap(properties));
    assertPublishedIds(doc);
  }

  private void assertPublishedIds(BaleenDocument doc) {
    when(documentProvider.getById(eq("testDoc"))).thenReturn(Mono.just(doc));
    postQuery(
        corpusQuery("document(id: \"testDoc\") { info { publishedIds } }"),
        defaultVariables())
            .jsonPath("$.data.corpus.document.info.publishedIds").isArray()
            .jsonPath("$.data.corpus.document.info.publishedIds[0]").isEqualTo("pubId");
  }

}
