package io.committed.ketos.plugins.data.mongo.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import io.committed.invest.support.data.utils.CriteriaUtils;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.input.DocumentFilter.DocumentInfoFilter;

public final class DocumentFilters {

  private DocumentFilters() {
    // Singleton
  }


  public static List<CriteriaDefinition> createCriteria(@Nullable final DocumentFilter documentFilter) {
    final List<CriteriaDefinition> list = new LinkedList<>();

    if (documentFilter == null) {
      return list;
    }

    Criteria criteria = new Criteria();


    // Text search must be first (in aggregations)
    if (documentFilter.getContent() != null) {
      list.add(TextCriteria.forDefaultLanguage().matching(documentFilter.getContent()));
    }


    if (documentFilter.getInfo() != null) {
      final DocumentInfoFilter info = documentFilter.getInfo();
      if (info.getCaveats() != null) {
        criteria = criteria.and("document.caveats").in(info.getCaveats());
      }

      if (info.getReleasability() != null) {
        criteria = criteria.and("document.releasability").in(info.getReleasability());
      }

      if (info.getEndTimestamp() != null) {
        criteria = criteria.and("document.timestamp").lte(info.getEndTimestamp());
      }

      if (info.getStartTimestamp() != null) {
        criteria = criteria.and("document.timestamp").gte(info.getStartTimestamp());
      }

      if (info.getLanguage() != null) {
        criteria = criteria.and("document.language").is(info.getLanguage());
      }

      if (info.getSource() != null) {
        criteria = criteria.and("document.source").is(info.getSource());
      }

      if (info.getType() != null) {
        criteria = criteria.and("document.type").is(info.getType());
      }
    }

    if (documentFilter.getMetadata() != null) {
      for (final Map.Entry<String, Object> e : documentFilter.getMetadata().entrySet()) {
        criteria = criteria.and("metadata." + e.getKey()).is(e.getValue());
      }
    }

    if (documentFilter.getPublishedIds() != null) {
      criteria = criteria.and("publishedIds").in(documentFilter.getPublishedIds());
    }

    list.add(criteria);


    return list;
  }


  public static Query createQuery(final DocumentFilter documentFilter) {
    return CriteriaUtils.createQuery(createCriteria(documentFilter));
  }
}
