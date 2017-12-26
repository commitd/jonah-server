package io.committed.ketos.data.elasticsearch.providers;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.server.data.providers.AbstractDataProvider;
import io.committed.invest.server.data.providers.DatabaseConstants;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchDocumentProvider extends AbstractDataProvider
    implements DocumentProvider {

  private final EsDocumentService documentService;

  public ElasticsearchDocumentProvider(final String dataset, final String datasource,
      final EsDocumentService documentService) {
    super(dataset, datasource);
    this.documentService = documentService;
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.ELASTICSEARCH;
  }

  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return documentService.findById(id).map(EsDocument::toBaleenDocument);
  }

  @Override
  public Flux<BaleenDocument> all(final int offset, final int size) {
    return search("*", offset, size);
  }



  @Override
  public Flux<BaleenDocument> search(final String search, final int offset, final int size) {
    return documentService.search(search, offset, size).map(EsDocument::toBaleenDocument);
  }

  @Override
  public Mono<Long> count() {
    return documentService.count();
  }

  @Override
  public Flux<TermBin> countByType() {
    return documentService.countByType();

  }

  @Override
  public Flux<TimeBin> countByDate() {
    return documentService.countByDate();
  }

  @Override
  public Mono<Long> countSearchMatches(final String query) {
    return documentService.countSearchMatches(query);

  }

  @Override
  public Flux<TermBin> countByClassification() {
    return documentService.countByClassification();

  }

  @Override
  public Flux<TermBin> countByLanguage() {
    return documentService.countByLanguage();

  }


}
