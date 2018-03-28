package io.committed.ketos.common.constants;

/** Mongo Constants relating to Baleen collection, settings. */
public final class BaleenMongoConstants {

  // Settings for MongoDataProviders
  public static final String SETTING_URI = "uri";
  public static final String SETTING_DB = "db";
  public static final String SETTING_COLLECTION = "collection";

  // Defaults output
  public static final String DEFAULT_HOST = "localhost";
  public static final int DEFAULT_PORT = 27017;

  public static final String DEFAULT_DATABASE = "baleen";
  public static final String DEFAULT_DOCUMENT_COLLECTION = "documents";
  public static final String DEFAULT_MENTION_COLLECTION = "mentions";
  public static final String DEFAULT_ENTITY_COLLECTION = "entities";
  public static final String DEFAULT_RELATION_COLLECTION = "relations";

  private BaleenMongoConstants() {
    // Singleton
  }
}
