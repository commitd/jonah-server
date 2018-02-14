package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsMentionProviderFactory
    extends AbstractElasticsearchDataProviderFactory<MentionProvider> {

  private final ObjectMapper mapper;


  public EsMentionProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-mentions", MentionProvider.class, "baleen", "mention");
    this.mapper = mapper;
  }


  @Override
  public Mono<MentionProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final ElasticsearchTemplate elastic = buildElasticTemplate(settings);

      final EsMentionService service =
          new EsMentionService(mapper, elastic, getIndexName(settings), getTypeName(settings));

      // return Mono.just(new ElasticsearchMentionProvider(dataset, datasource, service));
      return Mono.empty();

    } catch (final Exception e) {
      log.error("Unable to create ES Mention Provider", e);
      return Mono.empty();
    }
  }

}
