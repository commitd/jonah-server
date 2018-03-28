package io.committed.ketos.graphql.baleen;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.committed.invest.extensions.InvestGraphQlExtension;

/**
 * Extension which adds GraphQL resolvers for Baleen.
 *
 * <p>These back off to the Baleen Data Providers and data types as defined in ketos-common.
 */
@Configuration
@ComponentScan(basePackageClasses = BaleenGraphQlExtension.class)
public class BaleenGraphQlExtension implements InvestGraphQlExtension {}
