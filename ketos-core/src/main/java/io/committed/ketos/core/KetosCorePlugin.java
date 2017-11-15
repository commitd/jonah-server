package io.committed.ketos.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.ketos.core.config.KetosCoreSettings;
import io.committed.vessel.extensions.VesselGraphQlExtension;

@Configuration
@ComponentScan(basePackageClasses = { KetosCorePlugin.class })
@EnableConfigurationProperties(KetosCoreSettings.class)
public class KetosCorePlugin implements VesselGraphQlExtension {



}
