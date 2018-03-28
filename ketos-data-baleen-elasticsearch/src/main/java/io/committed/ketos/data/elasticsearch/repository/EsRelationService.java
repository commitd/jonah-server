package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.baleenconsumer.OutputRelation;
import io.committed.ketos.common.constants.BaleenProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * Relation specific ES support service.
 */
@Slf4j
public class EsRelationService extends AbstractEsBaleenService<OutputRelation> {

  public EsRelationService(final Client client, final ObjectMapper mapper, final String indexName,
      final String typeName) {
    super(client, mapper, indexName, typeName, OutputRelation.class);
  }

  public void updateSource(final String id, final OutputMention mention) {
    updateMention(BaleenProperties.RELATION_SOURCE, id, mention);

  }

  public void updateTarget(final String id, final OutputMention mention) {
    updateMention(BaleenProperties.RELATION_TARGET, id, mention);
  }

  protected void updateMention(final String field, final String id, final OutputMention mention) {

    final BoolQueryBuilder find = QueryBuilders.boolQuery()
        .must(QueryBuilders.typeQuery(getType()))
        .must(QueryBuilders.matchQuery(String.format("%s.%s", field, BaleenProperties.EXTERNAL_ID), id));

    scroll(find, OutputRelation.class, (i, r) -> {
      if (field.equalsIgnoreCase(BaleenProperties.RELATION_SOURCE)) {
        r.setSource(mention);
      } else {
        r.setTarget(mention);
      }

      update(r.getDocId(), i, getType(), r);
    });
  }


}
