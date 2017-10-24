package io.committed.ketos.data.elasticsearch.dao;

import org.springframework.data.annotation.Id;

import lombok.Data;


@Data
public class EsRelation {
  @Id
  private String id;

  private String externalId;

  private String docId;

  private String source;

  private String target;

  private int begin;

  private int end;

  private String type;

  private String relationshipType;

  private String relationSubtype;

  private String value;

  private double confidence;
}
