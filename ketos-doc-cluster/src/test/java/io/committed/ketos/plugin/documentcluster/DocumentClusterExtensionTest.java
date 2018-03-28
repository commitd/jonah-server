package io.committed.ketos.plugin.documentcluster;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.committed.invest.server.graphql.GraphQlConfig;
import io.committed.invest.test.InvestTestContext;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {InvestTestContext.class, DocumentClusterExtension.class})
@Import({GraphQlConfig.class})
@DirtiesContext
public class DocumentClusterExtensionTest {

  @Autowired DocumentClusterExtension extension;

  @Test
  public void test() {
    assertThat(extension).isNotNull();

    assertThat(extension.getName()).isNotBlank();
    assertThat(extension.getDescription()).isNotBlank();
    assertThat(extension.getId()).isNotBlank();
  }
}
