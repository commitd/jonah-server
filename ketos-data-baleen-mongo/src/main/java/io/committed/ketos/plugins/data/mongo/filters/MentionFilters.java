package io.committed.ketos.plugins.data.mongo.filters;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.data.geo.Box;
import org.springframework.data.geo.Point;
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
      criteria = criteria.and(mentionsPrefix + "value").gte(mentionFilter.getStartTimestamp());
    }


    if (mentionFilter.getEndTimestamp() != null) {
      criteria = criteria.and(mentionsPrefix + "value").lte(mentionFilter.getEndTimestamp());
    }

    if (mentionFilter.getWithin() != null) {
      final GeoBox within = mentionFilter.getWithin();
      final Box box = new Box(
          new Point(within.getSafeE(), within.getN()),
          new Point(within.getSafeW(), within.getS()));
      criteria = criteria.and(mentionsPrefix + "geoJson").within(box);
    }


    return criteria;
  }
}
