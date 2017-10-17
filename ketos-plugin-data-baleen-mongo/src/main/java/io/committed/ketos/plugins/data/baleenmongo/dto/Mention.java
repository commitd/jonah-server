package io.committed.dto;

import java.util.function.Function;

import io.committed.ketos.dao.BaleenMention;
import io.leangen.graphql.annotations.GraphQLId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mention {

  @GraphQLId
  private String id;
  private double confidence;
  private int begin;
  private int end;
  private String type;
  private String value;
  private String entityId;

  public Mention(String entityId, BaleenMention baleen) {
    this.entityId = entityId;
    id = baleen.getExternalId();
    confidence = baleen.getConfidence();
    begin = baleen.getBegin();
    end = baleen.getEnd();
    type = baleen.getType();
    value = baleen.getValue();
  }


  public static class MentionFactory implements Function<BaleenMention, Mention> {

    private final String entityId;

    public MentionFactory(String entityId) {
      this.entityId = entityId;
    }

    @Override
    public Mention apply(BaleenMention mention) {
      return new Mention(entityId, mention);
    }
  }
}
