package io.committed.ketos.plugins.data.mongo.filters;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.data.mongodb.core.query.Criteria;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.input.MentionFilter;

public final class EntityFilters {

  private EntityFilters() {
    // Singleton
  }

  public static List<Criteria> createCriteria(final Collection<EntityFilter> entityFilters,
      final String prefix) {
    final List<Criteria> list = new LinkedList<>();
    for (final EntityFilter f : entityFilters) {
      list.add(createCriteria(f, prefix));
    }
    return list;
  }

  public static Criteria createCriteria(final EntityFilter entityFilter) {
    return createCriteria(entityFilter, "");
  }

  public static Criteria createCriteria(@Nullable final EntityFilter entityFilter, final String prefix) {

    // TODO Currently this byproduct of the way Baleen Mongo stores data.. .in effect we work at the
    // mention level rather than at the entity level
    // Probably not the same thing in some cases, but until Baleen changes to creating real entities
    // (merged mention & properties) it's best generic approach.

    final MentionFilter mentionFilter = new MentionFilter();

    if (entityFilter != null) {
      mentionFilter.setDocId(entityFilter.getDocId());
      mentionFilter.setEntityId(entityFilter.getId());
    }

    return MentionFilters.createCriteria(mentionFilter);
  }
}
