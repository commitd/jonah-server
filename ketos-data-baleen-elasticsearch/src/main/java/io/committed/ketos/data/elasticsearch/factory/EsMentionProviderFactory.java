package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchMentionProvider;
import io.committed.ketos.data.elasticsearch.repository.EsDocumentService;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class EsMentionProviderFactory
    extends AbstractElasticsearchDataProviderFactory<MentionProvider> {

  private final ObjectMapper mapper;


  public EsMentionProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-mentions", MentionProvider.class);
    this.mapper = mapper;
  }


  @Override
  public Mono<MentionProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsDocumentService documents = new EsDocumentService(mapper, elastic);
      final EsMentionService mentions = new EsMentionService(documents);

      return Mono.just(new ElasticsearchMentionProvider(dataset, datasource, mentions));
    } catch (final Exception e) {
      log.error("Unable to create ES Document Provider", e);
      return Mono.empty();
    }
  }

}
