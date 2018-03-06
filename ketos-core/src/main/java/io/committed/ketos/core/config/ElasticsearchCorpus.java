package io.committed.ketos.core.config;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import io.committed.ketos.common.constants.BaleenElasticsearchConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ElasticsearchCorpus extends AbstractMultipleDataProviderDataDefinition {

  private String host = BaleenElasticsearchConstants.DEFAULT_HOST;
  private int port = BaleenElasticsearchConstants.DEFAULT_PORT;
  private String cluster = BaleenElasticsearchConstants.DEFAULT_CLUSTER;

  private String index = BaleenElasticsearchConstants.DEFAULT_INDEX;

  private String documents = BaleenElasticsearchConstants.DEFAULT_DOCUMENT_TYPE;
  private String entities = BaleenElasticsearchConstants.DEFAULT_ENTITY_TYPE;
  private String relations = BaleenElasticsearchConstants.DEFAULT_RELATION_TYPE;
  private String mentions = BaleenElasticsearchConstants.DEFAULT_MENTION_TYPE;

  public ElasticsearchCorpus() {
    super("baleen-elasticsearch", "Elasticsearch Mongo", "Baleen output stored in Elasticsearch", "es");
  }

  @Override
  protected Map<String, Object> getCrudMentionProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, mentions)
        .put("entityType", entities)
        .put("relationType", relations)
        .build();
  }

  @Override
  protected Map<String, Object> getCrudRelationProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, relations)
        .build();
  }

  @Override
  protected Map<String, Object> getCrudEntityProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, entities)
        .build();
  }

  @Override
  protected Map<String, Object> getCrudDocumentProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, documents)
        .put("mentionType", mentions)
        .put("entityType", entities)
        .put("relationType", relations)
        .build();
  }

  @Override
  protected Map<String, Object> getMentionProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, mentions)
        .build();
  }

  @Override
  protected Map<String, Object> getRelationProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, relations)
        .build();
  }

  @Override
  protected Map<String, Object> getEntityProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, entities)
        .build();
  }

  @Override
  protected Map<String, Object> getDocumentProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, documents)
        .put("mentionType", mentions)
        .put("entityType", entities)
        .put("relationType", relations)
        .build();
  }

  @Override
  protected Map<String, Object> getMetadataProviderSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_TYPE, documents)
        .build();
  }

  @Override
  protected ImmutableMap<String, Object> getBaseSettings() {
    return newSettings()
        .put(BaleenElasticsearchConstants.SETTING_CLUSTER, cluster)
        .put(BaleenElasticsearchConstants.SETTING_HOST, host)
        .put(BaleenElasticsearchConstants.SETTING_INDEX, index)
        .put(BaleenElasticsearchConstants.SETTING_PORT, port)
        .build();
  }


}
