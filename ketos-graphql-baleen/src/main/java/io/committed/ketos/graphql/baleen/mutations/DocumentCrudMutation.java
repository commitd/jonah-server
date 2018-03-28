package io.committed.ketos.graphql.baleen.mutations;

import java.util.Optional;

import reactor.core.publisher.Flux;

import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.query.DataHints;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.providers.baleen.CrudDocumentProvider;
import io.committed.ketos.common.references.BaleenDocumentReference;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;

/** CRUD mutations for documents. */
@GraphQLService
public class DocumentCrudMutation
    extends AbstractCrudMutation<BaleenDocumentReference, BaleenDocument, CrudDocumentProvider> {

  public DocumentCrudMutation(final DataProviders dataProviders) {
    super(dataProviders, BaleenDocument.class, CrudDocumentProvider.class);
  }

  @GraphQLMutation(name = "deleteDocument", description = "Delete document by id")
  public Flux<DataProvider> deleteDocument(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "reference") @GraphQLNonNull final BaleenDocumentReference id,
      @GraphQLArgument(
            name = "hints",
            description =
                "Provide hints about the datasource or database which should be used to execute this query"
          )
          final DataHints hints) {

    return delete(Optional.ofNullable(datasetId), id, hints);
  }

  @GraphQLMutation(name = "saveDocument", description = "Save document by id")
  public Flux<DataProvider> saveDocument(
      @GraphQLArgument(name = "datasetId") final String datasetId,
      @GraphQLArgument(name = "document") @GraphQLNonNull final BaleenDocument item,
      @GraphQLArgument(
            name = "hints",
            description =
                "Provide hints about the datasource or database which should be used to execute this query"
          )
          final DataHints hints) {

    return save(Optional.ofNullable(datasetId), item, hints);
  }
}
