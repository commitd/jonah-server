package io.committed.ketos.plugin.documentcluster;

import java.util.Arrays;
import java.util.List;
import io.committed.ketos.common.data.BaleenDocument;
import reactor.core.publisher.Flux;

public final class DocumentFixtures {
  // Headlines from wiki news (creative common attribution)
  // Taken 22 March 2018
  // See https://en.wikinews.org/wiki/Main_Page

  public static final List<BaleenDocument> DOCUMENTS = Arrays.asList(
      BaleenDocument.builder().id("1")
          .content("Kenyan conservancy euthanises last male northern white rhino; only two females remain").build(),
      BaleenDocument.builder().id("2")
          .content("Uber suspends self-driving car program after pedestrian death in Arizona, United States").build(),
      BaleenDocument.builder().id("3").content("Vladimir Putin wins fourth term as President of Russia").build(),
      BaleenDocument.builder().id("4").content("British scientist Stephen Hawking dies aged 76").build(),
      BaleenDocument.builder().id("5")
          .content("US toy retail giant Toys 'R' Us files for liquidation in United States").build(),
      BaleenDocument.builder().id("6")
          .content("State of emergency in Sri Lanka remains despite calm returning after violence").build(),
      BaleenDocument.builder().id("7")
          .content("Mirror's exposé prompts call for inquiry into child abuse ring in Telford, England").build(),
      BaleenDocument.builder().id("8").content("United States President Trump dismisses Secretary of State Tillerson")
          .build(),
      BaleenDocument.builder().id("9").content("China ends presidential term limits in constitutional amendment")
          .build());


  private DocumentFixtures() {
    // Singleon
  }

  public static Flux<BaleenDocument> flux() {
    return Flux.fromIterable(DOCUMENTS);
  }


}
