package io.committed.ketos.data.elasticsearch.providers;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import io.committed.vessel.core.dto.analytic.TermBin;
import io.committed.vessel.core.dto.analytic.TimeBin;
import io.committed.vessel.server.data.providers.AbstractDataProvider;
import io.committed.vessel.server.data.providers.DatabaseConstants;
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
  public Mono<BaleenDocument> getById(final String id) {
    return documentService.findById(id).map(EsDocument::toBaleenDocument);
  }

  @Override
  public Flux<BaleenDocument> all(final int offset, final int size) {
    // TODO: Throw error or just return nothing?
    // return Flux.error(new Exception("Not supported"));
    return Flux.empty();
  }

  @Override
  public String getDatabase() {
    return DatabaseConstants.ELASTICSEARCH;
  }

  @Override
  public Flux<BaleenDocument> search(final String search, final int offset, final int size) {
    return documentService.search(search, offset, size).map(EsDocument::toBaleenDocument);
  }

  // TODO: Allt hese

  @Override
  public Mono<Long> count() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Flux<TermBin> countByType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Flux<TimeBin> countByDate() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Mono<Long> countSearchMatches(final String query) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Flux<TermBin> countByClassification() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Flux<TermBin> countByLanguage() {
    // TODO Auto-generated method stub
    return null;
  }


}
