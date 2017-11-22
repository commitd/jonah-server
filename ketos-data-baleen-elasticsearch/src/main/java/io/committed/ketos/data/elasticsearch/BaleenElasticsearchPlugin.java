package io.committed.ketos.data.elasticsearch;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.committed.vessel.extensions.VesselDataExtension;

@Configuration
@Import(BaleenElasticsearchConfig.class)
public class BaleenElasticsearchPlugin implements VesselDataExtension {


}
