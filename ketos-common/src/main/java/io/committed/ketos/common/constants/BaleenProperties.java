package io.committed.ketos.common.constants;

public final class BaleenProperties {

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

  // Mention and entity properties

  public static final String START_TIMESTAMP = "timestampStart";
  public static final String STOP_TIMESTAMP = "timestampStop";
  public static final String GEOJSON = "geoJson";
  public static final String POI = "poi";
  public static final String TEMPORAL_PRECISION = "precision";

  private BaleenProperties() {
    // Singleton
  }
}
