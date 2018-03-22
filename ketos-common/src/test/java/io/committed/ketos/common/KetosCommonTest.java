package io.committed.ketos.common;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import io.committed.invest.test.InvestTestContext;


@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {InvestTestContext.class, KetosCommonExtension.class})
@DirtiesContext
public class KetosCommonTest {

  @Autowired
  private KetosCommonExtension extension;

  @Test
  public void testExtension() {
    assertThat(extension).isNotNull();

    assertThat(extension.getName()).isNotBlank();
    assertThat(extension.getDescription()).isNotBlank();
    assertThat(extension.getId()).isNotBlank();
  }

}
