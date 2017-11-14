package io.committed.ketos.ui.corpussummary;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configurable settings for the CorpusSummary plugin.
 *
 * Use the Spring application.properties or application.yaml to set properties.
 *
 * For example:
 *
 * If you have
 *
 * <pre>
 * private int maxResults = 10;
 * </pre>
 *
 * Then in Yaml you can override this with:
 *
 * <pre>
 * CorpusSummary:
 *  max-results: 100
 * </pre>
 *
 * You can access all the settings from your UI plugin on the browser with a GraphQL query:
 *
 * <pre>
 * query {
 *  vesselServer: {
 *   plugin(id:"CorpusSummary") {
 *     settings
 *   }
 *  }
 * }
 * </pre>
 *
 *
 */
@Configuration
@ConfigurationProperties("corpus-summary")
@Data 
public class CorpusSummarySettings {

  // TODO: Create any settings you need here
}
