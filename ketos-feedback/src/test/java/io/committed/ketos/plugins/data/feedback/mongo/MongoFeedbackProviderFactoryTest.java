package io.committed.ketos.plugins.data.feedback.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.committed.ketos.plugins.data.feedback.MongoTestResource;
import io.committed.ketos.plugins.data.feedback.data.Feedback;
import io.committed.ketos.plugins.data.feedback.data.FeedbackDataProvider;

public class MongoFeedbackProviderFactoryTest {

  private MongoTestResource resource;

  @Before
  public void before() {
    resource = new MongoTestResource();
    resource.setupMongo();
  }

  @After
  public void after() {
    resource.clearMongo();
  }

  @Test
  public void test() {

    final MongoFeedbackProviderFactory factory = new MongoFeedbackProviderFactory();
    final FeedbackDataProvider dataProvider =
        factory.build("testDataset", "testDatasource", resource.getSettings()).block();

    final Feedback feedback = Feedback.builder().build();

    assertThat(dataProvider.findAll(0, 10).count().block()).isEqualTo(0);

    dataProvider.save(feedback).block();

    final List<Feedback> list = dataProvider.findAll(0, 10).collectList().block();
    assertThat(list).hasSize(1);

    final Feedback found = list.get(0);

    assertThat(found).isEqualTo(feedback);

    dataProvider.delete(found.getId());

    assertThat(dataProvider.findAll(0, 10).count().block()).isEqualTo(0);
  }
}
