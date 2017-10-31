package io.committed.ketos.data.elasticsearch.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DatasourceConstants;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ElasticsearchDocumentProvider implements DocumentProvider {

  private final String corpus;
  private final EsDocumentService documentService;

  @Autowired
  public ElasticsearchDocumentProvider(final String corpus,
      final EsDocumentService documentService) {
    this.corpus = corpus;
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

  @Override
  public String getDatasource() {
    return DatasourceConstants.ELASTICSEARCH;
  }

  @Override
  public String getCorpus() {
    return corpus;
  }

}
