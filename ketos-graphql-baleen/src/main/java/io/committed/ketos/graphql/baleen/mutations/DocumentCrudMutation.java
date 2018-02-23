package io.committed.ketos.graphql.baleen.mutations;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.invest.support.data.AbstractCrudMutation;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.CrudDocumentProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import reactor.core.publisher.Flux;

@GraphQLService
public class DocumentCrudMutation extends AbstractCrudMutation<BaleenDocument, CrudDocumentProvider> {

  public DocumentCrudMutation(final DataProviders dataProviders) {
    super(dataProviders, BaleenDocument.class, CrudDocumentProvider.class);
  }

  @GraphQLMutation(name = "deleteDocument", description = "Delete document by id")
  public Flux<DataProvider> deleteDocument(
      @GraphQLArgument(name = "id") @GraphQLNonNull final String id,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return delete(id, hints);
  }

  @GraphQLMutation(name = "saveDocument", description = "Save document by id")
  public Flux<DataProvider> saveDocument(
      @GraphQLArgument(name = "id") @GraphQLNonNull final BaleenDocument item,
      @GraphQLArgument(name = "hints",
          description = "Provide hints about the datasource or database which should be used to execute this query") final DataHints hints) {

    return save(item, hints);
  }

}
