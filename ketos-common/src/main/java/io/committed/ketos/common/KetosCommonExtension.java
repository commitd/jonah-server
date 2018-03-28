package io.committed.ketos.common;

import org.springframework.context.annotation.Configuration;

import io.committed.invest.extensions.InvestExtension;

/** Extension for common data types in ketos */
@Configuration
public class KetosCommonExtension implements InvestExtension {

  @Override
  public String getName() {
    return "Ketos Common Library";
  }

  @Override
  public String getDescription() {
    return "Ketos common data and functions";
  }
}
