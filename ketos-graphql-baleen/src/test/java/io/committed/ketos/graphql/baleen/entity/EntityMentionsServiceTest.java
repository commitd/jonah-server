package io.committed.ketos.graphql.baleen.entity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusEntityService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class EntityMentionsServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusEntityService CorpusEntityService(DataProviders providers) {
      return new CorpusEntityService(providers);
    }

    @Bean
    public EntityMentionsService entityDocumentsService(DataProviders dataProviders) {
      return new EntityMentionsService(dataProviders);
    }

    @Bean
    public MentionProvider mentionProvider() {
      return GraphqlTestConfiguration.getMockedProvider(MentionProvider.class);
    }

    @Bean
    public EntityProvider entityProvider() {
      return GraphqlTestConfiguration.getMockedProvider(EntityProvider.class);
    }
  }

  @Autowired private MentionProvider mentionProvider;

  @Autowired private EntityProvider entityProvider;

  @Test
  public void testGetMentionsFromEntity() {
    BaleenEntity entity = new BaleenEntity("testEnt", "testDoc", "test", "", "testing", null);
    when(entityProvider.getById(anyString())).thenReturn(Mono.just(entity));
    BaleenMention mention =
        new BaleenMention("testMention", 0, 0, "test", "", "value", "eId", "docId", null);
    MentionSearchResult result = new MentionSearchResult();
    result.setResults(Flux.fromIterable(Collections.singletonList(mention)));
    when(mentionProvider.search(any(MentionSearch.class), anyInt(), anyInt())).thenReturn(result);
    postQuery(corpusQuery("entity(id: \"1\"){ mentions { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.entity.mentions")
        .isArray()
        .jsonPath("$.data.corpus.entity.mentions[0].id")
        .isEqualTo("testMention");
  }
}
