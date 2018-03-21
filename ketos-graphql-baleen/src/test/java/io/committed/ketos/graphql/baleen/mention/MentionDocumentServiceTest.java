package io.committed.ketos.graphql.baleen.mention;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusMentionService;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MentionDocumentServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusMentionService corpusMentionService(DataProviders dataProviders) {
      return new CorpusMentionService(dataProviders);
    }

    @Bean
    public MentionDocumentService entitySearchService(DataProviders dataProviders) {
      return new MentionDocumentService(dataProviders);
    }

    @Bean
    public MentionProvider mentionProvider() {
      return GraphqlTestConfiguration.getMockedProvider(MentionProvider.class);
    }

    @Bean
    public DocumentProvider documentProvider() {
      return GraphqlTestConfiguration.getMockedProvider(DocumentProvider.class);
    }
  }

  @Autowired
  private MentionProvider mentionProvider;

  @Autowired
  private DocumentProvider documentProvider;

  @Test
  public void testGetDocument() {
    BaleenMention mention = new BaleenMention("mid", 0, 1, "test", "", "val", "", "testDoc", null);
    when(mentionProvider.getById(anyString())).thenReturn(Mono.just(mention));
    when(documentProvider.getById(eq("testDoc"))).thenReturn(Mono.just(getTestDoc()));
    postQuery(corpusQuery("mention(id: \"1\"){ document { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.mention.document.id").isEqualTo("testDoc");
  }

}
