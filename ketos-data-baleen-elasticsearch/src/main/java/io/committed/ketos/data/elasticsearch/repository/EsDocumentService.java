package io.committed.ketos.data.elasticsearch.repository;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.ElasticsearchSupportService;
import io.committed.ketos.common.baleenconsumer.OutputDocument;

public class EsDocumentService extends ElasticsearchSupportService<OutputDocument> {

  public EsDocumentService(final ObjectMapper mapper, final ElasticsearchTemplate elastic, final String indexName,
      final String typeName) {
    super(mapper, elastic, indexName, typeName, OutputDocument.class);
  }

}
