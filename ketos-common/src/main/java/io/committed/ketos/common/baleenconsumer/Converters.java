package io.committed.ketos.common.baleenconsumer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.committed.invest.core.dto.collections.PropertiesMap;
import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenDocumentMetadata;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenMention.BaleenMentionBuilder;
import io.committed.ketos.common.data.BaleenRelation;

/**
 * Convert from Baleen output (Ouput*) to Ketos Baleen* POJOs and vice versa.
 *
 * <p>Largely a direct mapping.
 */
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

  public static List<BaleenDocumentMetadata> toBaleenDocumentMetadata(
      final List<OutputDocumentMetadata> metadata) {
    return metadata != null
        ? metadata.stream().map(Converters::toBaleenDocumentMetadata).collect(Collectors.toList())
        : Collections.emptyList();
  }

  public static BaleenDocumentMetadata toBaleenDocumentMetadata(
      final OutputDocumentMetadata metadata) {
    return BaleenDocumentMetadata.builder()
        .key(metadata.getKey())
        .value(metadata.getValue())
        .build();
  }

  public static BaleenEntity toBaleenEntity(final OutputEntity entity) {
    return BaleenEntity.builder()
        .docId(entity.getDocId())
        .id(entity.getExternalId())
        .properties(new PropertiesMap(entity.getProperties()))
        .subType(entity.getSubType())
        .type(entity.getType())
        .value(entity.getValue())
        .build();
  }

  public static BaleenMention toBaleenMention(final OutputMention mention) {

    final BaleenMentionBuilder builder = BaleenMention.builder();

    // Mention can be null if the relation is 'corrupt'...
    if (mention != null) {
      builder
          .begin(mention.getBegin())
          .docId(mention.getDocId())
          .end(mention.getEnd())
          .entityId(mention.getEntityId())
          .id(mention.getExternalId())
          .properties(new PropertiesMap(mention.getProperties()))
          .subType(mention.getSubType())
          .type(mention.getType())
          .value(mention.getValue());
    }

    return builder.build();
  }

  public static BaleenRelation toBaleenRelation(final OutputRelation relation) {
    return BaleenRelation.builder()
        .begin(relation.getBegin())
        .docId(relation.getDocId())
        .end(relation.getEnd())
        .id(relation.getExternalId())
        .properties(new PropertiesMap(relation.getProperties()))
        .source(toBaleenMention(relation.getSource()))
        .subType(relation.getSubType())
        .target(toBaleenMention(relation.getTarget()))
        .type(relation.getType())
        .value(relation.getValue())
        .build();
  }

  public static OutputDocument toOutputDocument(final BaleenDocument item) {
    final OutputDocument o = new OutputDocument();
    o.setContent(item.getContent());
    o.setExternalId(item.getId());
    o.setMetadata(
        item.getMetadata()
            .stream()
            .map(m -> new OutputDocumentMetadata(m.getKey(), m.getValue()))
            .collect(Collectors.toList()));
    o.setProperties(item.getProperties().asMap());
    return o;
  }

  public static OutputEntity toOutputEntity(final BaleenEntity item) {
    final OutputEntity o = new OutputEntity();
    o.setDocId(item.getDocId());
    o.setExternalId(item.getId());
    // NOTE: mentions will be lost here. If we wanted to fix that we'd to actually get the entity
    // and
    // then put it here
    o.setProperties(item.getProperties().asMap());
    o.setSubType(item.getSubType());
    o.setType(item.getType());
    o.setValue(item.getValue());
    return o;
  }

  public static OutputRelation toOutputRelation(final BaleenRelation item) {
    final OutputRelation o = new OutputRelation();
    o.setDocId(item.getDocId());
    o.setBegin(item.getBegin());
    o.setEnd(item.getEnd());
    o.setExternalId(item.getId());
    o.setProperties(item.getProperties().asMap());
    o.setSubType(item.getSubType());
    o.setType(item.getType());
    o.setValue(item.getValue());
    o.setBegin(item.getBegin());
    o.setEnd(item.getEnd());
    if (item.getSource() != null) {
      o.setSource(toOutputMention(item.getSource()));
    }
    if (item.getTarget() != null) {
      o.setTarget(toOutputMention(item.getTarget()));
    }
    return o;
  }

  public static OutputMention toOutputMention(final BaleenMention item) {
    final OutputMention o = new OutputMention();
    o.setBegin(item.getBegin());
    o.setEnd(item.getEnd());
    o.setDocId(item.getDocId());
    o.setExternalId(item.getId());
    o.setEntityId(item.getEntityId());
    o.setProperties(item.getProperties().asMap());
    o.setSubType(item.getSubType());
    o.setType(item.getType());
    o.setValue(item.getValue());
    return o;
  }
}
