package io.committed.ketos.plugins.graphql.baleenservices.services;

import java.util.Arrays;
import java.util.List;

import io.committed.ketos.plugins.data.baleen.BaleenCorpus;
import io.committed.vessel.extensions.graphql.VesselGraphQlService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;

@VesselGraphQlService
public class CorpusService {

  @GraphQLQuery(name = "corpus")
  public BaleenCorpus corpus(@GraphQLNonNull @GraphQLArgument(name = "id") final String id) {
    if (id.equals("baleen")) {
      return new BaleenCorpus("baleen", "Baleen Mongo");
    } else {
      return null;
    }
  }

  @GraphQLQuery(name = "corpora")
  public List<BaleenCorpus> corpora() {
    // TODO: This would be looked up from repo in future
    return Arrays.asList(new BaleenCorpus("baleen", "Baleen Mongo"));
  }
}
