package io.committed.ketos.common.baleenconsumer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import io.committed.invest.core.utils.FieldUtils;
import io.committed.ketos.common.constants.BaleenProperties;

/**
 * Helper to deal with mapping in Elasticsearch
 *
 * <p>When Elasticsearch is setup a mapping is minimal applied (see ElasticsearchKetos in
 * Baleen-collectionreaders). In an effort to stay standardised with ES we have the following
 * convention:
 *
 * <ul>
 *   <li>Any field which is of a very specific type (time / geo) is mapped specifically.
 *   <li>Any of the very core fields (eg docId, begin, end) is mapped specifically
 *   <li>Anything else is left to ES, which means typically it's text with an inner field of
 *       keyword.
 * </ul>
 *
 * For search this works well. If you search on properties.value you get a free text search, but for
 * aggregations you need to map to properties.value.keyword IF YOU ARE IN A TEXT field...
 *
 * <p>So this class attempts to capture what ES should have done.
 *
 * <p>(In theory we could probably read the ES mapping and determine this ourselves, but that's a
 * lot to parse).
 */
public class ElasticsearchMapping {

  private static final Set<String> NON_KEYWORD_PROPERTY_FIELD =
      Sets.newHashSet(
          BaleenProperties.POI,
          BaleenProperties.GEOJSON,
          BaleenProperties.TIMESTAMP,
          BaleenProperties.DOCUMENT_DATE,
          BaleenProperties.START_TIMESTAMP,
          BaleenProperties.STOP_TIMESTAMP,
          BaleenProperties.SENTENCE_DISTANCE,
          BaleenProperties.DOCUMENT_DISTANCE,
          BaleenProperties.WORD_DISTANCE,
          BaleenProperties.QUANTITY,
          BaleenProperties.AMOUNT,
          BaleenProperties.CONFIDENCE,
          BaleenProperties.IS_NORMALISED,
          BaleenProperties.NORMALISED_QUANTITY);

  private ElasticsearchMapping() {
    // Singleton
  }

  public static List<String> toAggregationPath(final List<String> path) {
    return getAggregationField(0, path);
  }

  public static String toAggregationField(final List<String> path) {
    return FieldUtils.joinField(toAggregationPath(path));
  }

  protected static List<String> getAggregationField(final int offset, final List<String> path) {
    final int size = path.size() - offset;

    if (size == 0) {
      // If empty just return as is
      // If path is single then we are in the root and that's always mapped to keyword/number/etc
      return path;
    }

    if (size == 1 && path.get(offset).equals(BaleenProperties.VALUE)) {
      // Value is a text / keyword
      return addKeyword(path);
    }

    if (path.get(offset).equals(BaleenProperties.METADATA)) {
      // if its key then that's a keyword
      if (size > 2) {
        // No idea what is could be, leave it alone
        return path;
      } else if (path.get(offset + 1).equalsIgnoreCase(BaleenProperties.METADATA_KEY)) {
        // Key is a keyword anyway
        return path;
      } else if (path.get(offset + 1).equalsIgnoreCase(BaleenProperties.METADATA_VALUE)) {
        // METADATA_VALUE is text, so we want to aggregate over keyword
        return addKeyword(path);
      }
    }

    if (path.get(offset).equals(BaleenProperties.PROPERTIES)) {
      // if its key then that's a keyword
      if (size > 2) {
        // No idea what is could be, leave it alone
        return path;
      } else {
        return getAggregationFieldForProperties(path, path.get(offset + 1));
      }
    }

    // Deal with sub documents in relations

    if (path.get(offset).equals(BaleenProperties.RELATION_SOURCE)
        || path.get(offset).equals(BaleenProperties.RELATION_TARGET)) {
      return getAggregationField(offset + 1, path);
    }

    // Nothing we recognise, return what we have
    return path;
  }

  private static List<String> getAggregationFieldForProperties(
      final List<String> path, final String field) {

    if (NON_KEYWORD_PROPERTY_FIELD.contains(field)) {
      return path;
    } else {
      return addKeyword(path);
    }
  }

  private static List<String> addKeyword(final List<String> path) {
    final List<String> newPath = new LinkedList<>(path);
    newPath.add("keyword");
    return newPath;
  }
}
