package io.committed.ketos.graphql.baleen.mentionsearch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.intermediate.MentionSearchResult;
import io.committed.ketos.common.graphql.output.MentionSearch;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import io.committed.ketos.graphql.baleen.corpus.CorpusMentionService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class MentionSearchServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {

    @Bean
    public CorpusMentionService corpusMentionService(DataProviders providers) {
      return new CorpusMentionService(providers);
    }

    @Bean
    public MentionSearchService documentSearchService(DataProviders dataProviders) {
      return new MentionSearchService(dataProviders);
    }

    @Bean
    public DocumentProvider dataProvider() {
      return GraphqlTestConfiguration.getMockedProvider(DocumentProvider.class);
    }

    @Bean
    public MentionProvider mentionProvider() {
      return GraphqlTestConfiguration.getMockedProvider(MentionProvider.class);
    }
  }

  @Autowired
  private MentionProvider mentionProvider;

  @Test
  public void testGetMentionSearchHits() {
    BaleenMention mention = new BaleenMention("mid", 0, 0, "test", "", "value", "eid", "docId", null);
    MentionSearchResult result = new MentionSearchResult();
    result.setResults(Flux.fromIterable(Collections.singletonList(mention)));
    result.setSize(1);
    result.setTotal(Mono.just(1l));
    result.setOffset(0);
    when(mentionProvider.search(any(MentionSearch.class), anyInt(), anyInt())).thenReturn(result);

    postQuery(corpusQuery("searchMentions(query: {id: \"mid\"}) { hits { results { id } } }"), defaultVariables())
        .jsonPath("$.data.corpus.searchMentions.hits.results").isArray()
        .jsonPath("$.data.corpus.searchMentions.hits.results[0].id").isEqualTo("mid");
  }

}
