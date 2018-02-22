package io.committed.ketos.plugins.data.mongo.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.committed.invest.core.dto.analytic.TermBin;
import lombok.Data;

@Data
public class AnyTermBin {
  @JsonProperty("term")
  private Object term;

  @JsonProperty("count")
  private long count;

  public TermBin toTermBin() {
    return new TermBin(term.toString(), count);
  }
}
