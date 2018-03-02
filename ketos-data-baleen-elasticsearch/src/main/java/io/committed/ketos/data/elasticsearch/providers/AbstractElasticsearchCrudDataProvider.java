package io.committed.ketos.data.elasticsearch.providers;


import org.elasticsearch.index.query.QueryBuilders;
import io.committed.invest.extensions.data.providers.CrudDataProvider;
import io.committed.invest.support.data.elasticsearch.AbstractElasticsearchDataProvider;
import io.committed.invest.support.data.elasticsearch.ElasticsearchSupportService;
import io.committed.ketos.common.constants.BaleenProperties;

public abstract class AbstractElasticsearchCrudDataProvider<R, T>
    extends AbstractElasticsearchDataProvider
    implements CrudDataProvider<R, T> {

  protected AbstractElasticsearchCrudDataProvider(final String dataset, final String datasource) {
    super(dataset, datasource);
  }

  protected boolean delete(final ElasticsearchSupportService<?> service, final String docId, final String externalId) {
    return delete(service, docId, BaleenProperties.EXTERNAL_ID, externalId);
  }

  protected boolean delete(final ElasticsearchSupportService<?> service, final String docId,
      final String externalIdField, final String externalId) {
    return service.delete(
        QueryBuilders.boolQuery()
            .must(QueryBuilders.matchQuery(externalIdField, externalId))
            .must(QueryBuilders.matchQuery(BaleenProperties.DOC_ID, docId)));
  }
}
