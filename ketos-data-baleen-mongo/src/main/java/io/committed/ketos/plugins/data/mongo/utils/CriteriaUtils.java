package io.committed.ketos.plugins.data.mongo.utils;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import io.committed.ketos.plugins.data.mongo.BooleanOperator;

public final class CriteriaUtils {
  private CriteriaUtils() {
    // Singleton
  }

  public static Criteria combineCriteria(final Collection<Criteria> list, final BooleanOperator operator) {
    Criteria combined = new Criteria();
    switch (operator) {
      case AND:
        for (final Criteria c : list) {
          combined = combined.andOperator(c);
        }
        break;
      case OR:
        for (final Criteria c : list) {
          combined = combined.orOperator(c);
        }
        break;
    }
    return combined;
  }


  public static Aggregation createAggregation(final Collection<CriteriaDefinition> matches,
      final AggregationOperation... operations) {
    final List<AggregationOperation> aggregations = new ArrayList<>();

    if (matches != null) {
      matches.stream().map(Aggregation::match).forEach(aggregations::add);
    }

    if (operations != null) {
      Arrays.stream(operations).forEach(aggregations::add);
    }

    return newAggregation(aggregations);

  }

  public static Query createQuery(final Collection<CriteriaDefinition> definitions) {
    final Query query = new Query();

    if (definitions != null) {
      definitions.forEach(query::addCriteria);
    }

    return query;
  }
}
