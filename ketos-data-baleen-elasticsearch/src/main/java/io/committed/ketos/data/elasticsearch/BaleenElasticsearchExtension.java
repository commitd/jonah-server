package io.committed.ketos.data.elasticsearch;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.committed.invest.extensions.InvestDataExtension;

@Configuration
@Import(BaleenElasticsearchConfig.class)
public class BaleenElasticsearchExtension implements InvestDataExtension {


}
