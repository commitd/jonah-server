package io.committed.ketos.plugins.data.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.committed.vessel.extensions.VesselDataExtension;

@Configuration
@Import(BaleenMongoConfig.class)
public class BaleenMongoPlugin implements VesselDataExtension {

}
