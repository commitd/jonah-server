// package io.committed.ketos.plugins.data.mongo.dao;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import org.springframework.data.annotation.Id;
// import org.springframework.data.mongodb.core.index.TextIndexed;
// import org.springframework.data.mongodb.core.mapping.Document;
// import io.committed.ketos.common.data.BaleenDocument;
// import io.committed.ketos.common.graphql.input.DocumentProbe;
// import lombok.Data;
// import lombok.NoArgsConstructor;
//
// @Data
// @Document(collection = "documents")
// @NoArgsConstructor
// public class MongoDocument {
//
// @Id
// private String id;
// private String externalId;
// private MongoDocumentInfo document;
// private List<String> publishedIds;
// private Map<String, Object> metadata = new HashMap<>();
// @TextIndexed
// private String content;
//
// public io.committed.ketos.common.data.BaleenDocument toDocument() {
// return BaleenDocument.builder()
// .id(getExternalId())
// .info(getDocument().toBaleenDocumentInfo())
// .publishedIds(publishedIds)
// .metadata(metadata)
// .content(content)
// .build();
// }
//
// public MongoDocument(final DocumentProbe probe) {
// this.externalId = probe.getId();
// this.content = probe.getContent();
// if (probe.getInfo() != null) {
// this.document = new MongoDocumentInfo(probe.getInfo());
// }
// this.publishedIds = probe.getPublishedIds();
// this.metadata = probe.getMetadata();
// }
// }
