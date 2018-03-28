package io.committed.ketos.plugins.data.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.committed.invest.extensions.InvestUiExtension;
import io.committed.invest.server.graphql.GraphQlConfig;
import io.committed.invest.test.InvestTestContext;
import io.committed.ketos.plugins.graphql.feedback.FeedbackGraphQlExtension;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(
  classes = {InvestTestContext.class, FeedbackGraphQlExtension.class, GraphQlConfig.class}
)
@DirtiesContext
public class FeedbackGraphQLExtensionTest {

  @Autowired FeedbackGraphQlExtension extension;

  @Test
  public void test() {
    assertThat(extension).isNotNull();

    assertThat(extension.getName()).isNotBlank();
    assertThat(extension.getDescription()).isNotBlank();
    assertThat(extension.getId()).isNotBlank();
  }

  @TestConfiguration
  public static class Configuration {

    @Bean
    public List<InvestUiExtension> uiExtensions() {
      return Collections.emptyList();
    }
  }
}
