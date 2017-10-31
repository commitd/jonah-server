package io.committed.ketos.plugins.data.corupus;

import java.util.Collections;
import java.util.List;

import io.committed.ketos.plugins.data.configurer.DataProviderSpecification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Corpus {

  private String id;

  private String name;

  private String description = "";

  private List<DataProviderSpecification> providers = Collections.emptyList();
}
