package io.committed.ketos.plugins.data.mongo.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bson.BsonDocument;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;
import org.bson.conversions.Bson;
import io.committed.ketos.common.baleenconsumer.OutputDocument;
import io.committed.ketos.common.baleenconsumer.OutputDocumentMetadata;
import io.committed.ketos.common.baleenconsumer.OutputEntity;
import io.committed.ketos.common.baleenconsumer.OutputFullDocument;
import io.committed.ketos.common.baleenconsumer.OutputLatLon;
import io.committed.ketos.common.baleenconsumer.OutputMention;
import io.committed.ketos.common.baleenconsumer.OutputRelation;

public final class BaleenCodecs {

  private BaleenCodecs() {
    // Singleton
  }

  public static CodecRegistry codecRegistry() {

    final PojoCodecProvider pojoCodecProvider = pojoCodecProviders();

    return CodecRegistries.fromRegistries(
        CodecRegistries.fromProviders(pojoCodecProvider),
        com.mongodb.MongoClient.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(new BsonCodecProvider()));
  }



  public static PojoCodecProvider pojoCodecProviders() {
    return PojoCodecProvider.builder().automatic(false)
        .register(new MapStringObjectPropertyCodecProvider())
        .register(OutputDocument.class)
        .register(OutputEntity.class)
        .register(OutputMention.class)
        .register(OutputRelation.class)
        .register(OutputFullDocument.class)
        .register(OutputDocumentMetadata.class)
        .register(OutputLatLon.class)
        .build();
  }


  // Mongo 3.6 driver does not support Map<String,Object> which is odd because it has a
  // a bson document is a Map<String,Object>.
  // There is a fix for this as noted in the issue:
  // https://jira.mongodb.org/browse/JAVA-2695
  // So this will not be required for much longer, but it ineffect just tells Mongo to
  // treat the Map<String,Object> as it would any old map - don't try and do something
  // clever with Object (as there's nothing clever to be done).

  public static class MapStringObjectPropertyCodecProvider implements PropertyCodecProvider {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> Codec<T> get(final TypeWithTypeParameters<T> type,
        final PropertyCodecRegistry registry) {
      // Check the main type and number of generic parameters
      if (Map.class.isAssignableFrom(type.getType())
          && type.getTypeParameters().size() == 2
          && type.getTypeParameters().get(0).getType().equals(String.class)
          && type.getTypeParameters().get(1).getType().equals(Object.class)) {

        return (Codec<T>) registry.get(MapType.INSTANCE);

      } else {
        return null;
      }
    }

    @SuppressWarnings({"rawtypes"})
    public static class MapType implements TypeWithTypeParameters<Map> {

      private static MapType INSTANCE = new MapType();

      @Override
      public Class<Map> getType() {
        return Map.class;
      }

      @Override
      public List<? extends TypeWithTypeParameters<?>> getTypeParameters() {
        return Collections.emptyList();
      }
    }
  }


  /**
   * This is a strange one...
   *
   * You cna define bson via Filters/Aggregates/etc or via new Document, but you can't mix them but
   * since there's no $cond on Filter/etc, there's nothing to do but mix them since we use Filters
   * everywhere.
   *
   * So here we help Mongo convert its own Bson converter.
   *
   * See https://jira.mongodb.org/browse/JAVA-1763
   */
  public static class BsonCodecProvider implements CodecProvider {

    @Override
    public <T> Codec<T> get(final Class<T> clazz, final CodecRegistry registry) {
      if (Bson.class.isAssignableFrom(clazz)) {
        return (Codec<T>) new BsonCodec(registry);
      }
      return null;
    }
  }

  public static class BsonCodec implements Codec<Bson> {

    private CodecRegistry codecRegistry;

    public BsonCodec(final CodecRegistry codecRegistry) {
      this.codecRegistry = codecRegistry;
    }

    @Override
    public void encode(final BsonWriter writer, final Bson value, final EncoderContext encoderContext) {
      final BsonDocument bsonDocument = value.toBsonDocument(Document.class, codecRegistry);
      final Codec<BsonDocument> codec = codecRegistry.get(BsonDocument.class);
      codec.encode(writer, bsonDocument, encoderContext);
    }

    @Override
    public Class<Bson> getEncoderClass() {
      return Bson.class;
    }

    @Override
    public Bson decode(final BsonReader reader, final DecoderContext decoderContext) {
      throw new UnsupportedOperationException("Decoding into a Bson is not allowed");

    }

  }
}
