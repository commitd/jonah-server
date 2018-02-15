package io.committed.ketos.common.constants;

public final class BaleenProperties {

  // General

  public static final String EXTERNAL_ID = "externalId";
  public static final String DOC_ID = "docId";
  public static final String METADATA = "metadata";
  public static final String METADATA_KEY = "key";
  public static final String METADATA_VALUE = "value";
  public static final String PROPERTIES = "properties";


  // Documentation based properties

  public static final String DOCUMENT_TYPE = "type";
  public static final String SOURCE = "source";
  public static final String LANGUAGE = "language";
  public static final String TIMESTAMP = "timestamp";
  public static final String CLASSIFICATION = "classification";
  public static final String CAVEATS = "caveats";
  public static final String RELEASABILITY = "releasability";
  public static final String HASH = "hash";
  public static final String DOCUMENT_DATE = "documentDate";
  public static final String DOCUMENT_TITLE = "documentTitle";
  public static final String PUBLISHED_IDS = "publishedIds";
  public static final String CONTENT = "content";

  // Common annotaiton propertiers

  public static final String BEGIN = "begin";
  public static final String END = "end";
  public static final String CONFIDENCE = "confidence";
  public static final String TYPE = "type";
  public static final String SUBTYPE = "subType";
  public static final String VALUE = "value";

  // Relation properties

  public static final String WORD_DISTANCE = "wordDistance";
  public static final String DEPENDENCY_DISTANCE = "dependencyDistance";
  public static final String NORMAL_SENTENCE_DISTANCE = "sentenceDistanceNormalized";
  public static final String NORMAL_WORD_DISTANCE = "wordDistanceNormalized";
  public static final String NORMAL_DEPENDENCY_DISTANCE = "dependencyDistanceNormalized";

  public static final String RELATION_TARGET = "target";
  public static final String RELATION_SOURCE = "source";

  // Mention and entity properties

  public static final String START_TIMESTAMP = "timestampStart";
  public static final String STOP_TIMESTAMP = "timestampStop";
  public static final String GEOJSON = "geoJson";
  public static final String POI = "poi";
  public static final String TEMPORAL_PRECISION = "precision";

  // Mention only
  public static final String ENTITY_ID = "entityId";

  // Entity only
  public static final String MENTION_IDS = "mentionIds";

  private BaleenProperties() {
    // Singleton
  }
}
