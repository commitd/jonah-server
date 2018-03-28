package io.committed.ketos.graphql.baleen.document;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.graphql.input.EntityProbe;
import io.committed.ketos.graphql.baleen.corpus.CorpusEntityService;
import io.committed.ketos.graphql.baleen.utils.AbstractGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import reactor.core.publisher.Flux;

/**
 * Entity resolvers which enhance the Document object.
 *
 */
@GraphQLService
public class DocumentEntityService extends AbstractGraphQlService {

  private final CorpusEntityService entityService;

  @Autowired
  public DocumentEntityService(final DataProviders corpusProviders, final CorpusEntityService entityService) {
    super(corpusProviders);
    this.entityService = entityService;
  }

  // Extend document

  @GraphQLQuery(name = "entities", description = "Get all entities")
  public Flux<BaleenEntity> getByDocument(@GraphQLContext final BaleenDocument document,
      @GraphQLArgument(name = "probe", description = "The type of the entity") final EntityProbe probe,
      @GraphQLArgument(name = "offset", defaultValue = "10") final int offset,
      @GraphQLArgument(name = "size", defaultValue = "10") final int size,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {


    final Optional<BaleenCorpus> corpus = document.findParent(BaleenCorpus.class);

    if (!corpus.isPresent()) {
      return Flux.empty();
    }

    EntityProbe documentProbe;
    if (probe != null) {
      documentProbe = probe;
    } else {
      documentProbe = new EntityProbe();
    }

    // This is the documetn we are looking for...
    documentProbe.setDocId(document.getId());

    return entityService.getEntities(corpus.get(), documentProbe, offset, size, hints);


  }



}

