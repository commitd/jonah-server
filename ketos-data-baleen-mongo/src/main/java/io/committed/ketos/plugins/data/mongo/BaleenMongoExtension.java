package io.committed.ketos.plugins.data.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.committed.invest.extensions.InvestDataExtension;

/**
 * Extension which provides data (providers and factories) for Baleen data providers which are
 * backed by Mongo.
 */
@Configuration
@Import(BaleenMongoConfig.class)
public class BaleenMongoExtension implements InvestDataExtension {}
