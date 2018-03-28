package io.committed.ketos.data.elasticsearch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/** Configuration root for extension. */
@Configuration
@ComponentScan(basePackageClasses = {BaleenElasticsearchConfig.class})
public class BaleenElasticsearchConfig {}
