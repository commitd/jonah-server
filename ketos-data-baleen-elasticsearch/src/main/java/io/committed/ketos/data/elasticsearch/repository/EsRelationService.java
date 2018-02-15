package io.committed.ketos.data.elasticsearch.repository;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.ketos.common.baleenconsumer.OutputRelation;

public class EsRelationService extends AbstractEsBaleenService<OutputRelation> {

  public EsRelationService(final ObjectMapper mapper, final ElasticsearchTemplate elastic, final String indexName,
      final String typeName) {
    super(mapper, elastic, indexName, typeName, OutputRelation.class);
  }

}
