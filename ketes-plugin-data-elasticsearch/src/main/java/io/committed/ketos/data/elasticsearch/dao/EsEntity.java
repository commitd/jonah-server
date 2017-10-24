package io.committed.ketos.data.elasticsearch.dao;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsEntity {

  private String externalId;

  private double confidence;

  private int begin;

  private int end;

  private String type;

  private String value;

  private Date timestampStart;

  private Date timestampStop;

  // TOOO: Verify that string works ok here
  private String geoJson;

}
