package io.committed.ketos.data.elasticsearch.factory;

import java.util.Map;
import org.elasticsearch.client.Client;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProviderFactory;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import io.committed.ketos.common.providers.baleen.MentionProvider;
import io.committed.ketos.data.elasticsearch.providers.ElasticsearchMentionProvider;
import io.committed.ketos.data.elasticsearch.repository.EsMentionService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EsMentionProviderFactory
    extends AbstractElasticsearchDataProviderFactory<MentionProvider> {

  private final ObjectMapper mapper;


  public EsMentionProviderFactory(final ObjectMapper mapper) {
    super("baleen-es-mentions", MentionProvider.class, BaleenElasticsearchConstants.DEFAULT_INDEX,
        BaleenElasticsearchConstants.DEFAULT_MENTION_TYPE);
    this.mapper = mapper;
  }


  @Override
  public Mono<MentionProvider> build(final String dataset, final String datasource,
      final Map<String, Object> settings) {
    try {
      final Client elastic = buildElasticClient(settings);

      final EsMentionService service =
          new EsMentionService(elastic, mapper, getIndexName(settings), getTypeName(settings));

      return Mono.just(new ElasticsearchMentionProvider(dataset, datasource, service));

    } catch (final Exception e) {
      log.error("Unable to create ES Mention Provider", e);
      return Mono.empty();
    }
  }

}
