package io.committed.ketos.plugins.data.mongo.data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;
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
        CodecRegistries.fromProviders(pojoCodecProvider), com.mongodb.MongoClient.getDefaultCodecRegistry());
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
}
