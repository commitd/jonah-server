package io.committed.ketos.plugins.data.mongo.data;

import static com.mongodb.assertions.Assertions.notNull;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWriter;
import org.bson.codecs.Encoder;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;

public final class CustomFilters {

  private CustomFilters() {
    // Singleton
  }

  public static <T> Bson eqFilter(final String field, final T value, final boolean forceOperator) {
    if (forceOperator) {
      return new EqFilter<T>(field, value);
    } else {
      return Filters.eq(field, value);
    }
  }

  // Filters.eq(key, value) becomes { key: value } but this does not work in the
  // places which require a boolean result (eg in $cond). So this Bson Filter
  // produces a full { $eq: [ key, value] }
  public static class EqFilter<TItem> implements Bson {
    private final String fieldName;
    private final TItem value;

    public EqFilter(final String fieldName, final TItem value) {
      this.fieldName = notNull("fieldName", fieldName);
      this.value = value;
    }

    @Override
    public <TDocument> BsonDocument toBsonDocument(final Class<TDocument> documentClass,
        final CodecRegistry codecRegistry) {
      final BsonDocumentWriter writer = new BsonDocumentWriter(new BsonDocument());

      writer.writeStartDocument();
      writer.writeStartArray("$eq");
      writer.writeString("$" + fieldName);
      encodeValue(writer, value, codecRegistry);
      writer.writeEndArray();
      writer.writeEndDocument();

      return writer.getDocument();
    }

    // Copied from from BuilderHelper
    private <TItem> void encodeValue(final BsonDocumentWriter writer, final TItem value,
        final CodecRegistry codecRegistry) {
      if (value == null) {
        writer.writeNull();
      } else if (value instanceof Bson) {
        ((Encoder) codecRegistry.get(BsonDocument.class)).encode(writer,
            ((Bson) value).toBsonDocument(BsonDocument.class, codecRegistry),
            EncoderContext.builder().build());
      } else {
        ((Encoder) codecRegistry.get(value.getClass())).encode(writer, value, EncoderContext.builder().build());
      }
    }

    @Override
    public String toString() {
      return "EqFilter{"
          + "fieldName='" + fieldName + '\''
          + ", value=" + value
          + '}';
    }
  }
}