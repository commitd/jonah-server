package io.committed.ketos.core.config;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import io.committed.ketos.common.constants.BaleenMongoConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MongoCorpus extends AbstractMultipleDataProviderDataDefinition {

  // Thse should match the settings in Mongo*ProviderFactory
  private static final String RELATION_COLLECTION = "relationCollection";
  private static final String ENTITY_COLLECTION = "entityCollection";
  private static final String MENTION_COLLECTION = "mentionCollection";

  private String host = BaleenMongoConstants.DEFAULT_HOST;
  private int port = BaleenMongoConstants.DEFAULT_PORT;

  private String db = BaleenMongoConstants.DEFAULT_DATABASE;

  private String documents = BaleenMongoConstants.DEFAULT_DOCUMENT_COLLECTION;
  private String entities = BaleenMongoConstants.DEFAULT_ENTITY_COLLECTION;
  private String relations = BaleenMongoConstants.DEFAULT_RELATION_COLLECTION;
  private String mentions = BaleenMongoConstants.DEFAULT_MENTION_COLLECTION;

  public MongoCorpus() {
    super("baleen-mongo", "Baleen Mongo", "Baleen output from Mongo", "mongo");
  }

  @Override
  protected Map<String, Object> getCrudMentionProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, mentions)
        .build();
  }

  @Override
  protected Map<String, Object> getCrudRelationProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, relations)
        .build();
  }

  @Override
  protected Map<String, Object> getCrudEntityProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, entities)
        .build();
  }

  @Override
  protected Map<String, Object> getCrudDocumentProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, documents)
        .put(MENTION_COLLECTION, mentions)
        .put(ENTITY_COLLECTION, entities)
        .put(RELATION_COLLECTION, relations)
        .build();
  }

  @Override
  protected Map<String, Object> getMentionProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, mentions)
        .put(ENTITY_COLLECTION, entities)
        .put(RELATION_COLLECTION, relations)
        .build();
  }

  @Override
  protected Map<String, Object> getRelationProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, relations)
        .build();
  }

  @Override
  protected Map<String, Object> getEntityProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, entities)
        .build();
  }

  @Override
  protected Map<String, Object> getDocumentProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, documents)
        .put(MENTION_COLLECTION, mentions)
        .put(ENTITY_COLLECTION, entities)
        .put(RELATION_COLLECTION, relations)
        .build();
  }

  @Override
  protected Map<String, Object> getMetadataProviderSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_COLLECTION, documents)
        .build();
  }

  @Override
  protected ImmutableMap<String, Object> getBaseSettings() {
    return newSettings()
        .put(BaleenMongoConstants.SETTING_DB, db)
        .put(BaleenMongoConstants.SETTING_URI, toMongoUri(host, port))
        .build();
  }

  public static String toMongoUri(final String host, final int port) {
    return String.format("mongodb://%s:%d/", host, port);
  }

}
