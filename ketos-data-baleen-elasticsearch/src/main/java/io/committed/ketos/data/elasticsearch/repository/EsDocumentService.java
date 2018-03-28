package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.ketos.common.baleenconsumer.OutputDocument;

/**
 * Document specific ES support service.
 */
public class EsDocumentService extends AbstractEsBaleenService<OutputDocument> {

  public EsDocumentService(final Client client, final ObjectMapper mapper, final String indexName,
      final String typeName) {
    super(client, mapper, indexName, typeName, OutputDocument.class);
  }

}
