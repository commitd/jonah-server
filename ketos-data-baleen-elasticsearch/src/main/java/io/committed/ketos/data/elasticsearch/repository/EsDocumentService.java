package io.committed.ketos.data.elasticsearch.repository;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.ElasticsearchSupportService;
import io.committed.ketos.data.elasticsearch.dao.BaleenElasticsearchConstants;
import io.committed.ketos.data.elasticsearch.dao.EsDocument;

public class EsDocumentService extends ElasticsearchSupportService<EsDocument> {

  public EsDocumentService(final ObjectMapper mapper, final ElasticsearchTemplate elastic) {
    // TODO: Shouldn't index be configurable?
    super(mapper, elastic, BaleenElasticsearchConstants.DOCUMENT_INDEX,
        BaleenElasticsearchConstants.DOCUMENT_TYPE, EsDocument.class);
  }

}
