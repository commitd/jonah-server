package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.ketos.common.baleenconsumer.OutputEntity;

/** Entity specific ES support service. */
public class EsEntityService extends AbstractEsBaleenService<OutputEntity> {

  public EsEntityService(
      final Client client,
      final ObjectMapper mapper,
      final String indexName,
      final String typeName) {
    super(client, mapper, indexName, typeName, OutputEntity.class);
  }
}
