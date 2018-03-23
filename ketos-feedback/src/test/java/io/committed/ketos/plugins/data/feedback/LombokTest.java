package io.committed.ketos.plugins.data.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import org.junit.Test;
import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.plugins.data.feedback.data.Feedback;

public class LombokTest {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    mt.testClass(Feedback.class);

  }

  @Test
  public void testFeedbackBuilder() {
    final Feedback f =
        Feedback.builder().comment("comment").id("id").pluginId("pluginId").subject("subject").timestamp(Instant.now())
            .type("type").user("user").build();


    assertThat(f.getComment()).isEqualTo("comment");
    assertThat(f.getId()).isEqualTo("id");
    assertThat(f.getPluginId()).isEqualTo("pluginId");
    assertThat(f.getSubject()).isEqualTo("subject");
    assertThat(f.getType()).isEqualTo("type");
    assertThat(f.getUser()).isEqualTo("user");

  }

}
