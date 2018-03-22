package io.committed.ketos.example.ui.html;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import io.committed.invest.server.core.ServerCoreConfiguration;
import io.committed.invest.test.InvestTestContext;


@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {InvestTestContext.class, ExampleHtmlUi.class, ServerCoreConfiguration.class})
@DirtiesContext
public class ExampleHtmlTest {

  @Autowired
  private WebTestClient webClient;

  @Test
  public void getIndex() {
    this.webClient.get()
        .uri("/ui/example-html/index.html")
        .exchange()
        .expectStatus().is2xxSuccessful();
  }

}
