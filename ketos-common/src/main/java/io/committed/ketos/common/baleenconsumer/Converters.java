package io.committed.ketos.common.baleenconsumer;

import java.util.List;
import java.util.stream.Collectors;
import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentMetadata;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;

public final class Converters {

  private Converters() {
    // Singleton
  }

  public static BaleenDocument toBaleenDocument(final OutputDocument document) {
    return BaleenDocument.builder()
        .content(document.getContent())
        .id(document.getExternalId())
        .metadata(toBaleenDocumentMetadata(document.getMetadata()))
        .properties(new PropertiesMap(document.getProperties()))
        .build();
  }

  public static List<BaleenDocumentMetadata> toBaleenDocumentMetadata(final List<OutputDocumentMetadata> metadata) {
    return metadata.stream().map(Converters::toBaleenDocumentMetadata).collect(Collectors.toList());
  }

  public static BaleenDocumentMetadata toBaleenDocumentMetadata(final OutputDocumentMetadata metadata) {
    return BaleenDocumentMetadata.builder()
        .key(metadata.getKey())
        .value(metadata.getValue())
        .build();
  }

  public static BaleenDocument toBaleenDocument(final OutputFullDocument document) {
    return toBaleenDocument((OutputDocument) document);
  }

  public static BaleenEntity toBaleenEntity(final OutputEntity entity) {
    return BaleenEntity.builder()
        .docId(entity.getDocId())
        .id(entity.getExternalId())
        .properties(entity.getProperties())
        .subType(entity.getSubType())
        .type(entity.getType())
        .value(entity.getValue())
        .build();
  }

  public static BaleenMention toBaleenMention(final OutputMention mention) {
    return BaleenMention.builder()
        .begin(mention.getBegin())
        .docId(mention.getDocId())
        .end(mention.getEnd())
        .entityId(mention.getEntityId())
        .id(mention.getExternalId())
        .properties(mention.getProperties())
        .subType(mention.getSubType())
        .type(mention.getType())
        .value(mention.getValue())
        .build();
  }

  public static BaleenRelation toBaleenRelation(final OutputRelation relation) {
    return BaleenRelation.builder()
        .begin(relation.getBegin())
        .docId(relation.getDocId())
        .end(relation.getEnd())
        .id(relation.getExternalId())
        .properties(relation.getProperties())
        .source(toBaleenMention(relation.getSource()))
        .subType(relation.getSubType())
        .target(toBaleenMention(relation.getTarget()))
        .type(relation.getType())
        .value(relation.getValue())
        .build();
  }
}
