package io.committed.ketos.core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.vessel.extensions.VesselGraphQlExtension;

@Configuration
@ComponentScan(basePackageClasses = { KetosCorePlugin.class })
public class KetosCorePlugin implements VesselGraphQlExtension {



}
