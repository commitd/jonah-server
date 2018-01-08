package io.committed.ketos.data.elasticsearch.providers;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.core.dto.analytic.TimeBin;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchServiceDataProvider;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.DocumentProvider;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ElasticsearchDocumentProvider
    extends AbstractElasticsearchServiceDataProvider<EsDocument, EsDocumentService>
    implements DocumentProvider {

  public ElasticsearchDocumentProvider(final String dataset, final String datasource,
      final EsDocumentService documentService) {
    super(dataset, datasource, documentService);
  }


  @Override
  public Mono<BaleenDocument> getById(final String id) {
    return getService().findById(id).map(EsDocument::toBaleenDocument);
  }

  @Override
  public Flux<BaleenDocument> all(final int offset, final int size) {
    return search("*", offset, size);
  }

  @Override
  public Flux<BaleenDocument> search(final String search, final int offset, final int size) {
    return getService().search(search, offset, size).map(EsDocument::toBaleenDocument);
  }

  @Override
  public Mono<Long> count() {
    return getService().count();
  }

  @Override
  public Flux<TermBin> countByType() {
    return getService().countByType();

  }

  @Override
  public Flux<TimeBin> countByDate() {
    return getService().countByDate();
  }

  @Override
  public Mono<Long> countSearchMatches(final String query) {
    return getService().countSearchMatches(query);

  }

  @Override
  public Flux<TermBin> countByClassification() {
    return getService().countByClassification();

  }

  @Override
  public Flux<TermBin> countByLanguage() {
    return getService().countByLanguage();
  }

}
