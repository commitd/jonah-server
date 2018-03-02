package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.UpdateByQueryAction;
import org.elasticsearch.index.reindex.UpdateByQueryRequestBuilder;
import org.elasticsearch.script.Script;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.baleenconsumer.OutputRelation;
import io.committed.ketos.common.constants.BaleenProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsRelationService extends AbstractEsBaleenService<OutputRelation> {

  public EsRelationService(final Client client, final ObjectMapper mapper, final String indexName,
      final String typeName) {
    super(client, mapper, indexName, typeName, OutputRelation.class);
  }

  public void updateSource(final String id, final OutputMention mention) {
    updatMention(BaleenProperties.RELATION_SOURCE, id, mention);

  }

  public void updateTarget(final String id, final OutputMention mention) {
    updatMention(BaleenProperties.RELATION_TARGET, id, mention);
  }

  protected void updatMention(final String field, final String id, final OutputMention mention) {
    String value;
    try {
      value = getMapper().writeValueAsString(mention);
    } catch (final JsonProcessingException e) {
      log.warn("UNable to convert to save", e);
      return;
    }
    final Script script = new Script(String.format("ctx._source.%s = %s", field, value));

    final UpdateByQueryRequestBuilder ubqrb = UpdateByQueryAction.INSTANCE
        .newRequestBuilder(getClient())
        .source(getIndex())
        .script(script)
        .filter(QueryBuilders.boolQuery()
            .must(QueryBuilders.typeQuery(getType()))
            .must(QueryBuilders.matchQuery(String.format("%s.%s", field, BaleenProperties.EXTERNAL_ID), id)));



    try {
      ubqrb.execute().get();
    } catch (final Exception e) {
      log.warn("Ubable to execute update", e);
    }

    return;
  }
}
