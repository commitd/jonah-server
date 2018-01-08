package io.committed.ketos.graphql.baleen;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import io.committed.invest.extensions.InvestGraphQlExtension;

@Configuration
@ComponentScan(basePackageClasses = BaleenGraphQlExtension.class)
public class BaleenGraphQlExtension implements InvestGraphQlExtension {


}
