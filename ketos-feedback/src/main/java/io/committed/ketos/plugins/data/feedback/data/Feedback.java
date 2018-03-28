package io.committed.ketos.plugins.data.feedback.data;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Feedback (dto and dao)
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
