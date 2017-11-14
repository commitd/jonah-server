package io.committed.ketos.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupHousekeeper implements ApplicationListener<ContextRefreshedEvent> {

  @Autowired
  private KetosCoreSettings settings;

  @Override
  public void onApplicationEvent(final ContextRefreshedEvent event) {
    settings.getDatasets().forEach(System.out::println);
  }
}
