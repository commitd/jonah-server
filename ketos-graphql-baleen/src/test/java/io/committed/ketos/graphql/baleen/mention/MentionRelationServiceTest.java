package io.committed.ketos.graphql.baleen.mention;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.common.providers.baleen.RelationProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusMentionService;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MentionRelationServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusMentionService corpusMentionService(DataProviders dataProviders) {
      return new CorpusMentionService(dataProviders);
    }

    @Bean
    public MentionRelationService entitySearchService(DataProviders dataProviders) {
      return new MentionRelationService(dataProviders);
    }

    @Bean
    public MentionProvider mentionProvider() {
      return GraphqlTestConfiguration.getMockedProvider(MentionProvider.class);
    }

    @Bean
    public RelationProvider relationProvider() {
      return GraphqlTestConfiguration.getMockedProvider(RelationProvider.class);
    }
  }

  @Autowired private MentionProvider mentionProvider;

  @Autowired private RelationProvider relationProvider;

  @Test
  public void testGetSourceOf() {
    BaleenMention mention = new BaleenMention("mid", 0, 1, "test", "", "val", "", "testDoc", null);
    when(mentionProvider.getById(anyString())).thenReturn(Mono.just(mention));
    List<BaleenRelation> relations =
        Collections.singletonList(createRelation("rel", "source", "target"));
    when(relationProvider.getSourceRelations(eq(mention))).thenReturn(Flux.fromIterable(relations));
    postQuery(corpusQuery("mention(id: \"1\"){ sourceOf { id }}"), defaultVariables())
        .jsonPath("$.data.corpus.mention.sourceOf")
        .isArray()
        .jsonPath("$.data.corpus.mention.sourceOf[0].id")
        .isEqualTo("rel");
  }

  @Test
  public void testetTargetOf() {
    BaleenMention mention = new BaleenMention("mid", 0, 1, "test", "", "val", "", "testDoc", null);
    when(mentionProvider.getById(anyString())).thenReturn(Mono.just(mention));
    List<BaleenRelation> relations =
        Collections.singletonList(createRelation("rel", "source", "target"));
    when(relationProvider.getTargetRelations(eq(mention))).thenReturn(Flux.fromIterable(relations));
    postQuery(corpusQuery("mention(id: \"1\"){ targetOf { id }}"), defaultVariables())
        .jsonPath("$.data.corpus.mention.targetOf")
        .isArray()
        .jsonPath("$.data.corpus.mention.targetOf[0].id")
        .isEqualTo("rel");
  }
}
