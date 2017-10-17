package io.committed.query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.committed.dao.repository.BaleenRelationRepository;
import io.committed.dto.Document;
import io.committed.dto.Mention;
import io.committed.dto.Relation;
import io.committed.ketos.dao.BaleenRelation;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;

@Component
public class RelationService {

  @Autowired
  BaleenRelationRepository relations;
  @Autowired
  EntityService entityService;

  private Stream<Relation> toRelations(Stream<BaleenRelation> stream) {
    return stream.map(Relation::new);
  }

  private Stream<Relation> toRelations(List<BaleenRelation> findByDocId) {
    return toRelations(findByDocId.stream());
  }

  // FIXME: should be relations - current bug in spqr
  @GraphQLQuery(name = "allRelations")
  public Stream<Relation> getAllRelations(
      @GraphQLArgument(name = "limit", defaultValue = "0") int limit) {
    Stream<BaleenRelation> stream = StreamSupport.stream(relations.findAll().spliterator(), false);
    if (limit > 0) {
      stream = stream.limit(limit);
    }

    return toRelations(stream);
  }


  @GraphQLQuery(name = "relationsByDocument")
  public Stream<Relation> getByDocument(@GraphQLArgument(name = "id") @GraphQLId String id) {
    return toRelations(relations.findByDocId(id));
  }

  @GraphQLQuery(name = "relations")
  public Stream<Relation> getRelations(@GraphQLContext Document document) {
    return getByDocument(document.getId());
  }


  @GraphQLQuery(name = "sourceOf")
  public Stream<Relation> getSourceRelations(@GraphQLContext Mention mention) {
    return toRelations(relations.findBySource(mention.getId()));
  }

  @GraphQLQuery(name = "targetOf")
  public Stream<Relation> getTargetRelations(@GraphQLContext Mention mention) {
    return toRelations(relations.findByTarget(mention.getId()));
  }



  @GraphQLQuery(name = "relation")
  public Optional<Relation> getById(@GraphQLArgument(name = "id") @NotNull String id) {
    return relations.findByExternalId(id).map(Relation::new);
  }


}
