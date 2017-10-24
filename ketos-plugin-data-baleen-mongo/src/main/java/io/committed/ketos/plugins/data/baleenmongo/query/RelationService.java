package io.committed.ketos.plugins.data.baleenmongo.query;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;

import io.committed.ketos.plugins.data.baleenmongo.dao.BaleenRelation;
import io.committed.ketos.plugins.data.baleenmongo.dto.Document;
import io.committed.ketos.plugins.data.baleenmongo.dto.Mention;
import io.committed.ketos.plugins.data.baleenmongo.dto.Relation;
import io.committed.ketos.plugins.data.baleenmongo.repository.BaleenRelationRepository;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;


@VesselGraphQlService
public class RelationService {

  @Autowired
  BaleenRelationRepository relations;
  @Autowired
  EntityService entityService;

  private Stream<Relation> toRelations(final Stream<BaleenRelation> stream) {
    return stream.map(BaleenRelation::toRelation);
  }

  private Stream<Relation> toRelations(final List<BaleenRelation> findByDocId) {
    return toRelations(findByDocId.stream());
  }

  // FIXME: should be relations - current bug in spqr
  @GraphQLQuery(name = "allRelations")
  public Stream<Relation> getAllRelations(
      @GraphQLArgument(name = "limit", defaultValue = "0") final int limit) {
    Stream<BaleenRelation> stream = StreamSupport.stream(relations.findAll().spliterator(), false);
    if (limit > 0) {
      stream = stream.limit(limit);
    }

    return toRelations(stream);
  }


  @GraphQLQuery(name = "relationsByDocument")
  public Stream<Relation> getByDocument(@GraphQLArgument(name = "id") @GraphQLId final String id) {
    return toRelations(relations.findByDocId(id));
  }

  @GraphQLQuery(name = "relations")
  public Stream<Relation> getRelations(@GraphQLContext final Document document) {
    return getByDocument(document.getId());
  }


  @GraphQLQuery(name = "sourceOf")
  public Stream<Relation> getSourceRelations(@GraphQLContext final Mention mention) {
    return toRelations(relations.findBySource(mention.getId()));
  }

  @GraphQLQuery(name = "targetOf")
  public Stream<Relation> getTargetRelations(@GraphQLContext final Mention mention) {
    return toRelations(relations.findByTarget(mention.getId()));
  }



  @GraphQLQuery(name = "relation")
  public Optional<Relation> getById(@GraphQLArgument(name = "id") final String id) {
    return relations.findByExternalId(id).map(BaleenRelation::toRelation);
  }


}
