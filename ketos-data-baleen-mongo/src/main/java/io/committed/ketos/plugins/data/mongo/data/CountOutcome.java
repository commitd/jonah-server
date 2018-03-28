package io.committed.ketos.plugins.data.mongo.data;

import lombok.Data;

/**
 * Aggregation output for a count.
 */
@Data
public class CountOutcome {

  private long total;

}
