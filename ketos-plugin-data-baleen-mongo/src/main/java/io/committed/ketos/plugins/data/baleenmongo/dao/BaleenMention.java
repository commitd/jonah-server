package io.committed.ketos.plugins.data.baleenmongo.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaleenMention {

  private double confidence;
  private String externalId;
  private int begin;
  private int end;
  private String type;
  private String value;

}
