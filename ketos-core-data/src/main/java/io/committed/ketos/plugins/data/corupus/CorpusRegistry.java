package io.committed.ketos.plugins.data.corupus;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CorpusRegistry {

  private final List<Corpus> corpus;

  public CorpusRegistry(final List<Corpus> corpus) {
    this.corpus = corpus;
  }

  public Flux<Corpus> getCorpora() {
    return Flux.fromIterable(corpus);
  }

  public Mono<Corpus> findById(final String id) {
    return getCorpora()
        .filter(c -> c.getId().equalsIgnoreCase(id))
        .next();
  }
}
