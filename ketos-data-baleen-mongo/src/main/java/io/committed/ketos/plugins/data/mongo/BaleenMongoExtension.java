package io.committed.ketos.plugins.data.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.committed.invest.extensions.InvestDataExtension;

@Configuration
@Import(BaleenMongoConfig.class)
public class BaleenMongoExtension implements InvestDataExtension {

}
