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

import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.providers.baleen.EntityProvider;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusMentionService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MentionEntityServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusMentionService corpusMentionService(DataProviders dataProviders) {
      return new CorpusMentionService(dataProviders);
    }

    @Bean
    public MentionEntityService entitySearchService(DataProviders dataProviders) {
      return new MentionEntityService(dataProviders);
    }

    @Bean
    public MentionProvider mentionProvider() {
      return GraphqlTestConfiguration.getMockedProvider(MentionProvider.class);
    }

    @Bean
    public EntityProvider documentProvider() {
      return GraphqlTestConfiguration.getMockedProvider(EntityProvider.class);
    }
  }

  @Autowired private MentionProvider mentionProvider;

  @Autowired private EntityProvider entityProvider;

  @Test
  public void testGetEntity() {
    BaleenMention mention =
        new BaleenMention("mid", 0, 1, "test", "", "val", "entityId", "testDoc", null);
    BaleenEntity entity = new BaleenEntity("entityId", "", "", "", "", null);
    when(mentionProvider.getById(anyString())).thenReturn(Mono.just(mention));
    when(entityProvider.mentionEntity(eq(mention))).thenReturn(Mono.just(entity));
    postQuery(corpusQuery("mention(id: \"mid\"){ entity { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.mention.entity.id")
        .isEqualTo("entityId");
  }
}
