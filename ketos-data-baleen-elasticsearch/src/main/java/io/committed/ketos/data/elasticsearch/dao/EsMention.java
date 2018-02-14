// package io.committed.ketos.data.elasticsearch.dao;
//
// import java.util.Date;
// import java.util.HashMap;
// import java.util.Map;
// import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import io.committed.ketos.common.data.BaleenEntity;
// import io.committed.ketos.common.data.BaleenMention;
// import io.committed.ketos.common.data.BaleenMention.BaleenMentionBuilder;
// import lombok.Data;
// import lombok.extern.slf4j.Slf4j;
// import reactor.core.publisher.Mono;
//
// @Data
// @JsonIgnoreProperties(ignoreUnknown = true)
// @Slf4j
// public class EsMention {
//
// private static final ObjectMapper MAPPER = new ObjectMapper();
//
// private String externalId;
//
// private double confidence;
//
// private int begin;
//
// private int end;
//
// private String type;
//
// private String value;
//
// private Date timestampStart;
//
// private Date timestampStop;
//
// private Object geoJson;
//
// // Used to carry through flux, etc
// private transient String documentId;
//
// public BaleenEntity toBaleenEntity() {
// return BaleenEntity.builder().docId(documentId).id(externalId)
// .mentions(Mono.just(toBaleenMention(documentId)).flux()).build();
// }
//
// public BaleenMention toBaleenMention() {
// // This should only be used with documentId is non-null
// return toBaleenMention(documentId);
// }
//
// public BaleenMention toBaleenMention(final String documentId) {
//
//
// final BaleenMentionBuilder builder = BaleenMention.builder()
// .begin(begin)
// .end(end)
// .docId(documentId)
// .confidence(confidence)
// .entityId(externalId)
// .id(externalId)
// .type(type)
// .value(value);
//
//
// final Map<String, Object> properties = new HashMap<>();
//
// if (geoJson instanceof Map) {
// try {
// final String geoJsonString = MAPPER.writeValueAsString(geoJson);
// properties.put("geoJson", geoJsonString);
//
// } catch (final JsonProcessingException e) {
// // Ignore really..
// log.warn("Unable to serialise geoJson, because {}", e.getMessage());
// }
// }
//
// properties.put("timestampStart", timestampStart);
// properties.put("timestampStop", timestampStop);
//
//
// builder.properties(properties);
//
// return builder.build();
// }
//
// public static BaleenEntity toBaleenEntity(final String documentId, final BaleenMention mention) {
// return BaleenEntity.builder()
// .docId(documentId)
// .id(mention.getId())
// .mentions(Mono.just(mention).flux())
// .build();
// }
// }
