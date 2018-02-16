package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.client.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.ketos.common.baleenconsumer.OutputRelation;

public class EsRelationService extends AbstractEsBaleenService<OutputRelation> {

  public EsRelationService(final Client client, final ObjectMapper mapper, final String indexName,
      final String typeName) {
    super(client, mapper, indexName, typeName, OutputRelation.class);
  }

}
