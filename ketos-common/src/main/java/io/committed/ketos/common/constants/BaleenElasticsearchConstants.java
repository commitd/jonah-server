package io.committed.ketos.common.constants;

/** Elasticsearch constants related to Baleen indices/types/settings. */
public final class BaleenElasticsearchConstants {

  // Data Provider Factory constants
  // See AbstractElasticsearchDataProviderFactory

  public static final String SETTING_HOST = "host";
  public static final String SETTING_PORT = "port";
  public static final String SETTING_CLUSTER = "cluster";
  public static final String SETTING_TYPE = "type";
  public static final String SETTING_INDEX = "index";

  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 9300;
  public static final String DEFAULT_CLUSTER = "elasticsearch";

  // Default values for indexes

  public static final String DEFAULT_INDEX = "baleen";
  public static final String DEFAULT_MENTION_TYPE = "mention";
  public static final String DEFAULT_ENTITY_TYPE = "entity";
  public static final String DEFAULT_RELATION_TYPE = "relation";
  public static final String DEFAULT_DOCUMENT_TYPE = "document";

  private BaleenElasticsearchConstants() {
    // Singleton
  }
}
