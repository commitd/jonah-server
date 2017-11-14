package io.committed.vessel.plugin.data.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.committed.vessel.extensions.VesselDataExtension;

@Configuration
@Import(BaleenJpaPlugin.class)
public class BaleenJpaPlugin implements VesselDataExtension {


}
