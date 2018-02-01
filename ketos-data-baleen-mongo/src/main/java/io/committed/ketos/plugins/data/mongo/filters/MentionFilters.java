package io.committed.ketos.plugins.data.mongo.filters;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.core.query.Criteria;
import io.committed.invest.core.dto.analytic.GeoBox;
import io.committed.ketos.common.graphql.input.MentionFilter;

public final class MentionFilters {

  private MentionFilters() {
    // Singleton
  }

  public static List<Criteria> createCriteria(final Collection<MentionFilter> mentionFilters,
      final String entityPrefix, final String mentionsPrefix) {
    final List<Criteria> list = new LinkedList<>();
    for (final MentionFilter f : mentionFilters) {
      list.add(createCriteria(f, entityPrefix, mentionsPrefix));
    }
    return list;
  }


  public static Criteria createCriteria(@Nullable final MentionFilter mentionFilter, final String entityPrefix,
      final String mentionsPrefix) {
    Criteria criteria = new Criteria();

    if (mentionFilter == null) {
      return criteria;
    }

    // TODO: Need to sanitise MentionFilter somewhere...[before it gets here] eg if startTimestsamp is
    // set then it's
    // temporal... etc. You can't have within and start/endTimestamp etc because it
    // if you have startTimestamp you can add additional EXACT date type prop need to look up what that
    // is.

    // Entity stuff

    if (mentionFilter.getDocId() != null) {
      criteria = criteria.and(entityPrefix + "docId").is(mentionFilter.getDocId());
    }

    if (mentionFilter.getEntityId() != null) {
      criteria = criteria.and(entityPrefix + "id").is(mentionFilter.getEntityId());
    }

    // Now mention

    if (mentionFilter.getType() != null) {
      criteria = criteria.and(mentionsPrefix + "type").is(mentionFilter.getType());
    }

    if (mentionFilter.getValue() != null) {
      criteria = criteria.and(mentionsPrefix + "value").is(mentionFilter.getValue());
    }

    if (mentionFilter.getProperties() != null) {
      for (final Map.Entry<String, Object> e : mentionFilter.getProperties().entrySet()) {
        criteria = criteria.and(mentionsPrefix + e.getKey()).is(e.getValue());
      }
    }

    if (mentionFilter.getStartTimestamp() != null) {
      criteria = criteria.and(mentionsPrefix + "timestampStart").gte(mentionFilter.getStartTimestamp());
    }


    if (mentionFilter.getEndTimestamp() != null) {
      criteria = criteria.and(mentionsPrefix + "timestampEnd").lte(mentionFilter.getEndTimestamp());
    }

    if (mentionFilter.getWithin() != null) {
      final GeoBox within = mentionFilter.getWithin();

      // TODO Ideally we'd use a box and within as that's nice and easy...
      // but then we have lots of country stuff which is global (is colonies) so
      // so we need to us an intersection.
      // final Box box = new Box(
      // new Point(within.getSafeW(), within.getSafeS(),
      // new Point(within.getSafeE(), within.getSafeN());


      final Point bl = new Point(within.getSafeW(), within.getSafeS());
      final Point br = new Point(within.getSafeE(), within.getSafeS());
      final Point tr = new Point(within.getSafeE(), within.getSafeN());
      final Point tl = new Point(within.getSafeW(), within.getSafeN());

      final GeoJsonPolygon geoJson = new GeoJsonPolygon(bl, br, tr, tl, bl);

      // TODO: In either within on intersection, this won't actually find anything if the search is bigger
      // than a hemisphere!
      // there's a fix
      // https://docs.mongodb.com/manual/reference/operator/query/geoIntersects/#intersects-a-big-polygon
      // but you cant set the CRS in Sprign Data Mongo.

      criteria = criteria.and(mentionsPrefix + "geoJson").intersects(geoJson);
    }


    return criteria;
  }
}
