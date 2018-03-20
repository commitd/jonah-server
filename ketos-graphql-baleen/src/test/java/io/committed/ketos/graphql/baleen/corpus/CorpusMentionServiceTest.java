package io.committed.ketos.graphql.baleen.corpus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.graphql.input.MentionProbe;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.graphql.AbstractKetosGraphqlTest;
import io.committed.ketos.graphql.GraphqlTestConfiguration;
import io.committed.ketos.graphql.KetosGraphqlTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@KetosGraphqlTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CorpusMentionServiceTest extends AbstractKetosGraphqlTest {

  @TestConfiguration
  static class TestContextConfig {
    @Bean
    public MentionProvider relationProvider() {
      MentionProvider mock = Mockito.mock(MentionProvider.class);
      when(mock.getDataset()).thenReturn(GraphqlTestConfiguration.TEST_DATASET);
      when(mock.getDatabase()).thenReturn(GraphqlTestConfiguration.TEST_DB);
      when(mock.getDatasource()).thenReturn(GraphqlTestConfiguration.TEST_DATASOURCE);
      return mock;
    }

    @Bean
    public CorpusMentionService service(DataProviders providers) {
      return new CorpusMentionService(providers);
    }
  }

  @Autowired
  private MentionProvider mentionProvider;

  @Test
  public void testGetById() {
    when(mentionProvider.getById(anyString())).thenReturn(Mono.just(createMention("1", "test")));
    postQuery(corpusQuery("mention(id:\"test\"){id value}"), defaultVariables())
        .jsonPath("$.data.corpus.mention.id").isEqualTo("1")
        .jsonPath("$.data.corpus.mention.value").isEqualTo("test");
  }

  @Test
  public void testGetEntities() {
    List<BaleenMention> results = Collections.singletonList(createMention("1", "test"));
    when(mentionProvider.getAll(anyInt(), anyInt())).thenReturn(Flux.fromIterable(results));
    postQuery(corpusQuery("mentions{ id value }"), defaultVariables())
        .jsonPath("$.data.corpus.mentions").isArray()
        .jsonPath("$.data.corpus.mentions[0].id").isEqualTo("1")
        .jsonPath("$.data.corpus.mentions[0].value").isEqualTo("test");
  }

  @Test
  public void testGetEntitiesWithProbe() {
    List<BaleenMention> results = Collections.singletonList(createMention("probe", "test"));
    when(mentionProvider.getByExample(any(MentionProbe.class), anyInt(), anyInt()))
        .thenReturn(Flux.fromIterable(results));
    postQuery(corpusQuery("mentions(probe: {id: \"probe\"}){ id value }"), defaultVariables())
        .jsonPath("$.data.corpus.mentions").isArray()
        .jsonPath("$.data.corpus.mentions[0].id").isEqualTo("probe")
        .jsonPath("$.data.corpus.mentions[0].value").isEqualTo("test");
  }

  @Test
  public void testSearchForEntities() {
    postQuery(corpusQuery("searchMentions(query: {id: \"test\"}){ mentionFilter { id } }"), defaultVariables())
        .jsonPath("$.data.corpus.searchMentions.mentionFilter.id").isEqualTo("test");
  }

  @Test
  public void testSearchForEntitiesNullArgs() {
    postQuery(corpusQuery("searchMentions{ mentionFilter { id } }"), defaultVariables())
        .jsonPath("$.errors").exists();
  }

  @Test
  public void testCountMentions() {
    when(mentionProvider.count()).thenReturn(Mono.just(10l));
    postQuery(corpusQuery("countMentions"), defaultVariables())
        .jsonPath("$.data.corpus.countMentions").isEqualTo(10);
  }

  @Test
  public void testCountByField() {
    List<TermBin> results = Collections.singletonList(new TermBin("test", 1));
    when(mentionProvider.countByField(any(), anyList(), anyInt())).thenReturn(Flux.fromIterable(results));
    postQuery(corpusQuery("countByMentionField(field: \"type\") { bins { term count } }"), defaultVariables())
        .jsonPath("$.data.corpus.countByMentionField.bins").isArray()
        .jsonPath("$.data.corpus.countByMentionField.bins[0].term").isEqualTo("test")
        .jsonPath("$.data.corpus.countByMentionField.bins[0].count").isEqualTo(1);
  }

  @Test
  public void testCountByFieldEmptyField() {
    postQuery(corpusQuery("countByMentionField(field: \"\") { bins { term count } }"), defaultVariables())
        .jsonPath("$.data.corpus.countByMentionField").isEqualTo(null);
  }

  @Test
  public void testCountByFieldNullField() {
    postQuery(corpusQuery("countByMentionField { bins { term count } }"), defaultVariables())
        .jsonPath("$.errors").exists();
  }

  private BaleenMention createMention(String id, String value) {
    return new BaleenMention(id, 0, 0, "test", "testSub", value, "testEntity", "testDoc", null);
  }



}
