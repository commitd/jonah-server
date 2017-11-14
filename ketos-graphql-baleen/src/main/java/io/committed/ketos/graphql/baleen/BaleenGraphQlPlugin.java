package io.committed.ketos.graphql.baleen;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.vessel.extensions.VesselGraphQlExtension;

@Configuration
@ComponentScan(basePackageClasses = BaleenGraphQlPlugin.class)
public class BaleenGraphQlPlugin implements VesselGraphQlExtension {


}
