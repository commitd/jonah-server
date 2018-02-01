package io.committed.ketos.graphql.baleen.root;

import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.dataset.Dataset;
import io.committed.invest.extensions.data.dataset.DatasetRegistry;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.ketos.common.data.BaleenCorpus;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GraphQLService
public class CorpusService {

  private final DatasetRegistry corpusRegistry;

  private final DataProviders corpusProviders;

  @Autowired
  public CorpusService(final DatasetRegistry corpusRegistry, final DataProviders corpusProviders) {
    this.corpusRegistry = corpusRegistry;
    this.corpusProviders = corpusProviders;

  }

  @GraphQLQuery(name = "corpus", description = "Access to a particular corpus dataset")
  public Mono<BaleenCorpus> corpus(@GraphQLNonNull @GraphQLArgument(name = "id",
      description = "The corpus id") final String id) {
    return corpusRegistry.findById(id).map(this::toBaleenCorpus);
  }

  @GraphQLQuery(name = "corpora", description = "Access to all corpora available")
  public Flux<BaleenCorpus> corpora(
      @GraphQLArgument(name = "provider") final String provider,
      @GraphQLArgument(name = "database") final String database,
      @GraphQLArgument(name = "datasource") final String datasource) {
    Flux<Dataset> flux = corpusRegistry.getDatasets();
    if (provider != null || database != null || datasource != null) {
      flux = flux.filterWhen(d -> filter(d, provider, database, datasource));

    }
    return flux.map(this::toBaleenCorpus);
  }

  private Mono<Boolean> filter(final Dataset dataset, final String provider, final String database,
      final String datasource) {

    return corpusProviders.findForDataset(dataset.getId())
        .any(p -> (provider == null || provider.equals(p.getProviderType()))
            && (database == null || database.equals(p.getDatabase()))
            && (datasource == null || datasource.equals(p.getDatasource())));
  }

  private BaleenCorpus toBaleenCorpus(final Dataset corpus) {
    return new BaleenCorpus(corpus.getId(), corpus.getName(), corpus.getDescription());
  }



}
