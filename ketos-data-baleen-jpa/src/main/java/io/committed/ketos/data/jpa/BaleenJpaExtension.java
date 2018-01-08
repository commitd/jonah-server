package io.committed.ketos.data.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import io.committed.invest.extensions.InvestDataExtension;

@Configuration
@Import(BaleenJpaConfig.class)
public class BaleenJpaExtension implements InvestDataExtension {


}
