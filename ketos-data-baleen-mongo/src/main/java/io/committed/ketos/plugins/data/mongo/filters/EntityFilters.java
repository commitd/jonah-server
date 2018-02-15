package io.committed.ketos.plugins.data.mongo.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import io.committed.invest.support.mongo.utils.FilterUtils;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.graphql.input.EntityFilter;
import io.committed.ketos.common.graphql.output.EntitySearch;

public final class EntityFilters {

  private EntityFilters() {
    // Singleton
  }

  public static Optional<Bson> createFilter(final Optional<EntityFilter> filter) {

    if (!filter.isPresent()) {
      return Optional.empty();
    }

    final EntityFilter entityFilter = filter.get();

    final List<Bson> filters = new LinkedList<>();

    if (entityFilter.getId() != null) {
      filters.add(Filters.eq(BaleenProperties.EXTERNAL_ID, entityFilter.getId()));
    }


    if (entityFilter.getDocId() != null) {
      filters.add(Filters.eq(BaleenProperties.DOC_ID, entityFilter.getDocId()));
    }

    return FilterUtils.combine(filters);
  }

  public static Optional<Bson> createFilter(final EntitySearch entitySearch) {
    final List<Bson> filters = new LinkedList<>();

    createFilter(Optional.ofNullable(entitySearch.getEntityFilter()))
        .ifPresent(filters::add);

    if (entitySearch.hasMentions()) {
      MentionFilters.createFilters(entitySearch.getMentionFilters().stream())
          .forEach(filters::add);
    }

    return FilterUtils.combine(filters);
  }


}
