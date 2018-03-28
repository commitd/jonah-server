package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.client.Client;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.ketos.common.baleenconsumer.OutputMention;

/** Mention specific ES support service. */
public class EsMentionService extends AbstractEsBaleenService<OutputMention> {

  public EsMentionService(
      final Client client,
      final ObjectMapper mapper,
      final String indexName,
      final String typeName) {
    super(client, mapper, indexName, typeName, OutputMention.class);
  }
}
