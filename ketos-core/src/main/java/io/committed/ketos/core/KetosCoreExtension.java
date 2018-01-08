package io.committed.ketos.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestGraphQlExtension;
import io.committed.ketos.core.config.KetosCoreSettings;

@Configuration
@ComponentScan(basePackageClasses = {KetosCoreExtension.class})
@EnableConfigurationProperties(KetosCoreSettings.class)
public class KetosCoreExtension implements InvestGraphQlExtension {



}
