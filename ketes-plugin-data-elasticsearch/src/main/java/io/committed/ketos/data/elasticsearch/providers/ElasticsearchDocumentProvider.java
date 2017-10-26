package io.committed.ketos.data.elasticsearch.providers;

import org.springframework.stereotype.Service;

import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import io.committed.ketos.plugins.data.baleen.BaleenDocument;
import io.committed.ketos.plugins.graphql.baleenservices.providers.DocumentProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ElasticsearchDocumentProvider implements DocumentProvider {

  private final EsDocumentService documentService;

  public ElasticsearchDocumentProvider(final EsDocumentService documentService) {
    this.documentService = documentService;
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return documentService.findById(id).map(EsDocument::toBaleenDocument);
  }

  @Override
  public Flux<BaleenDocument> search(final String search, final int limit) {
    return documentService.search(search, limit).map(EsDocument::toBaleenDocument);
  }

  @Override
  public Flux<BaleenDocument> all(final int limit) {
    // TODO: Throw error or just return nothing?
    // return Flux.error(new Exception("Not supported"));
    return Flux.empty();
  }

}
