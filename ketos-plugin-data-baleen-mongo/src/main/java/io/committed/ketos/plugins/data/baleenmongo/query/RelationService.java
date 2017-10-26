// package io.committed.ketos.plugins.data.baleenmongo.query;
//
// import org.springframework.beans.factory.annotation.Autowired;
//
// import io.committed.ketos.plugins.data.baleen.BaleenDocument;
// import io.committed.ketos.plugins.data.baleen.BaleenMention;
// import io.committed.ketos.plugins.data.baleen.BaleenRelation;
// import io.committed.ketos.plugins.data.baleenmongo.dao.MongoRelation;
// import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenRelationRepository;
// import io.committed.vessel.extensions.graphql.VesselGraphQlService;
// import io.leangen.graphql.annotations.GraphQLArgument;
// import io.leangen.graphql.annotations.GraphQLContext;
// import io.leangen.graphql.annotations.GraphQLId;
// import io.leangen.graphql.annotations.GraphQLQuery;
// import reactor.core.publisher.Flux;
// import reactor.core.publisher.Mono;
//
//
//
// public class RelationService {
//
// @Autowired
// BaleenRelationRepository relations;
//
//
// private Flux<BaleenRelation> toRelations(final Flux<MongoRelation> stream) {
// return stream.map(MongoRelation::toRelation);
// }
//
// // private Flux<Relation> toRelations(final List<BaleenRelation> findByDocId) {
// // return toRelations(findByDocId.stream());
// // }
//
// // FIXME: should be relations - current bug in spqr
// @GraphQLQuery(name = "allRelations")
// public Flux<BaleenRelation> getAllRelations(
// @GraphQLArgument(name = "limit", defaultValue = "0") final int limit) {
// Flux<MongoRelation> stream = relations.findAll();
// if (limit > 0) {
// stream = stream.take(limit);
// }
//
// return toRelations(stream);
// }
//
//
// @GraphQLQuery(name = "relationsByDocument")
// public Flux<BaleenRelation> getByDocument(
// @GraphQLArgument(name = "id") @GraphQLId final String id) {
// return toRelations(relations.findByDocId(id));
// }
//
// @GraphQLQuery(name = "relations")
// public Flux<BaleenRelation> getRelations(@GraphQLContext final BaleenDocument document) {
// return getByDocument(document.getId());
// }
//
//
// @GraphQLQuery(name = "sourceOf")
// public Flux<BaleenRelation> getSourceRelations(@GraphQLContext final BaleenMention mention) {
// return toRelations(relations.findBySource(mention.getId()));
// }
//
// @GraphQLQuery(name = "targetOf")
// public Flux<BaleenRelation> getTargetRelations(@GraphQLContext final BaleenMention mention) {
// return toRelations(relations.findByTarget(mention.getId()));
// }
//
//
//
// @GraphQLQuery(name = "relation")
// public Mono<BaleenRelation> getById(@GraphQLArgument(name = "id") final String id) {
// return relations.findByExternalId(id).map(MongoRelation::toRelation);
// }
//
//
// }
