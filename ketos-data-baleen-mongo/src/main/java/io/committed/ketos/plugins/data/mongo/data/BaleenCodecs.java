package io.committed.ketos.plugins.data.mongo.data;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
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

    return CodecRegistries.fromRegistries(com.mongodb.MongoClient.getDefaultCodecRegistry(),
        CodecRegistries.fromProviders(pojoCodecProvider));
  }



  public static PojoCodecProvider pojoCodecProviders() {
    return PojoCodecProvider.builder().automatic(true)
        .register(OutputDocument.class)
        .register(OutputEntity.class)
        .register(OutputMention.class)
        .register(OutputRelation.class)
        .register(OutputFullDocument.class)
        .register(OutputDocumentMetadata.class)
        .register(OutputLatLon.class)
        .build();
  }

}
