package io.committed.ketos.data.elasticsearch.repository;

import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.committed.ketos.data.elasticsearch.dao.EsDocument;
import io.committed.vesssel.support.elasticsearch.ReactiveElasticsearchUtils;
import io.committed.vesssel.support.elasticsearch.SourceUtils;
import reactor.core.publisher.Mono;

@Service
public class EsDocumentService {

  private static final String DOCUMENT_INDEX = "documents";
  private static final String DOCUMENT_TYPE = "document";

  private final Client client;
  private final ObjectMapper mapper;

  @Autowired
  public EsDocumentService(final ObjectMapper mapper, final ElasticsearchTemplate elastic) {
    this.mapper = mapper;
    this.client = elastic.getClient();
  }

  public Mono<EsDocument> findById(final String id) {
    final ListenableActionFuture<GetResponse> future = client.prepareGet()
        .setIndex(DOCUMENT_INDEX)
        .setType(DOCUMENT_TYPE)
        .setId(id)
        .execute();

    return ReactiveElasticsearchUtils.toMono(future)
        .flatMap(r -> SourceUtils.convertSource(mapper, r.getSourceAsString(),
            EsDocument.class));
  }


}
