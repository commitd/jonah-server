package io.committed.ketos.plugins.data.feedback.data;

import java.time.Instant;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feedback {

  @Id
  private String id;

  private String user;

  private String subject;

  private String pluginId;

  private String type;

  private String comment;

  private Instant timestamp;


}
