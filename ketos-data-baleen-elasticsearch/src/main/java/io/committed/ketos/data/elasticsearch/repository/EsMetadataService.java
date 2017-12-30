package io.committed.ketos.data.elasticsearch.repository;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.invest.core.dto.analytic.TermBin;
import io.committed.invest.support.data.elasticsearch.AbstractEsService;
import io.committed.ketos.data.elasticsearch.dao.BaleenElasticsearchConstants;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import reactor.core.publisher.Flux;

public class EsMetadataService extends AbstractEsService<EsDocument> {

  public EsMetadataService(final ObjectMapper mapper, final ElasticsearchTemplate elastic) {
    super(mapper, elastic, BaleenElasticsearchConstants.DOCUMENT_INDEX,
        BaleenElasticsearchConstants.DOCUMENT_TYPE, EsDocument.class);
  }

  public Flux<TermBin> countByKey(final String key) {
    // TODO: I don't think this is possible in the current Map<,> schema
    // I don't think we aggregate over the keys (ie I don't think we can refer to the ky?
    // It needs to be changed to an [ key: "", value: ""]
    return Flux.empty();
  }

  public Flux<TermBin> countByValue(final String key) {
    // TODO: As above I don't think this can be done in
    return Flux.empty();
  }

}
