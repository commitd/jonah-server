package io.committed.ketos.core.configuration;

import java.util.Collections;
import java.util.List;

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
