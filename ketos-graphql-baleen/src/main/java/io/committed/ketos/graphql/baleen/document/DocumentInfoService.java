package io.committed.ketos.graphql.baleen.document;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.ketos.common.constants.BaleenProperties;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentInfo;
import io.committed.ketos.common.data.BaleenDocumentInfo.BaleenDocumentInfoBuilder;
import io.committed.ketos.common.utils.MapUtils;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;


/**
 * Document info resolvers which enhance the Document object.
 *
 */
@GraphQLService
public class DocumentInfoService {

  @GraphQLQuery(name = "info", description = "A structured output of document information, built from properties")
  public BaleenDocumentInfo getInfo(@GraphQLContext final BaleenDocument document) {

    final BaleenDocumentInfoBuilder builder = BaleenDocumentInfo.builder();

    final Map<String, Object> properties = document.getProperties().asMap();

    builder.caveats(MapUtils.getStringsAsKey(properties, BaleenProperties.CAVEATS));
    builder.classification(MapUtils.getStringAsKey(properties, BaleenProperties.CLASSIFICATION).orElse(null));
    builder.date(MapUtils.getDateAsKey(properties, BaleenProperties.DOCUMENT_DATE).orElse(null));
    builder.language(MapUtils.getStringAsKey(properties, BaleenProperties.LANGUAGE).orElse("NA"));
    builder.releasability(MapUtils.getStringsAsKey(properties, BaleenProperties.RELEASABILITY));
    builder.source(MapUtils.getStringAsKey(properties, BaleenProperties.SOURCE).orElse(""));
    builder.timestamp(MapUtils.getDateAsKey(properties, BaleenProperties.TIMESTAMP).orElse(null));
    builder.title(MapUtils.getStringAsKey(properties, BaleenProperties.DOCUMENT_TITLE).orElse("Unknown"));
    builder.type(MapUtils.getStringAsKey(properties, BaleenProperties.DOCUMENT_TYPE).orElse("Unknown"));

    builder.publishedIds(extractPublishedIds(properties));

    final BaleenDocumentInfo info = builder.build();
    info.setParent(document);
    return info;
  }

  private Collection<String> extractPublishedIds(final Map<String, Object> properties) {
    final List<String> publishedIds = new LinkedList<>();

    final Object object = properties.get("publishedIds");
    if (object != null && object instanceof Collection) {
      final Collection<Object> c = (Collection<Object>) object;
      for (final Object o : c) {
        if (o == null) {
          // ignore nulls
        }
        if (o instanceof String) {
          publishedIds.add((String) o);
        } else {
          if (o instanceof Map) {
            final Map<Object, Object> m = (Map<Object, Object>) o;
            final Object id = m.get("id");
            if (id != null && id instanceof String) {
              publishedIds.add((String) id);
            }
          }
        }
      }
    }

    return publishedIds;
  }

}
