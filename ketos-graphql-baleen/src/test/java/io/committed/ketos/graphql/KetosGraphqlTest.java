package io.committed.ketos.graphql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import io.committed.invest.server.data.services.DefaultDatasetProviders;
import io.committed.invest.server.data.services.DefaultDatasetRegistry;
import io.committed.invest.server.graphql.GraphQlConfig;
import io.committed.ketos.graphql.baleen.corpus.CorpusProviderService;
import io.committed.ketos.graphql.baleen.root.CorpusService;

@WebFluxTest
@ContextConfiguration(
  classes = {
    JacksonAutoConfiguration.class,
    GraphqlTestConfiguration.class,
    DefaultDatasetProviders.class,
    CorpusService.class,
    DefaultDatasetRegistry.class,
    CorpusProviderService.class
  }
)
@Import({GraphQlConfig.class})
@DirtiesContext
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface KetosGraphqlTest {}
