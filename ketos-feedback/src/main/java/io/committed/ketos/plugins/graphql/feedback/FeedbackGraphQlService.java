package io.committed.ketos.plugins.graphql.feedback;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.util.StringUtils;

import io.committed.invest.annotations.GraphQLService;
import io.committed.invest.core.graphql.Context;
import io.committed.invest.extensions.InvestUiExtension;
import io.committed.invest.server.data.services.DatasetProviders;
import io.committed.ketos.plugins.data.feedback.data.Feedback;
import io.committed.ketos.plugins.data.feedback.data.FeedbackDataProvider;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@GraphQLService
public class FeedbackGraphQlService {

  private final DatasetProviders providers;

  private final List<InvestUiExtension> uiExtensions;

  public FeedbackGraphQlService(final DatasetProviders providers,
      final List<InvestUiExtension> uiExtensions) {
    this.providers = providers;
    this.uiExtensions = uiExtensions;
  }

  @GraphQLMutation(name = "addFeedback", description = "Save feedback")
  public Mono<Feedback> addFeedback(@GraphQLRootContext final Context context,
      @GraphQLArgument(name = "pluginId") final String pluginId,
      @GraphQLArgument(name = "subject") final String subject,
      @GraphQLArgument(name = "type") final String type,
      @GraphQLNonNull @GraphQLArgument(name = "comment") final String comment) {

    final String user =
        context.getAuthentication().map(Principal::getName).defaultIfEmpty("guest").block();

    final Feedback f = Feedback.builder()
        .comment(comment)
        .subject(subject)
        .type(type)
        .user(user)
        .pluginId(pluginId)
        .timestamp(Instant.now())
        .build();

    // Save into every feeback provider...
    return providers.findAll(FeedbackDataProvider.class)
        .flatMap(d -> d.save(f))
        .last();
  }

  @GraphQLMutation(name = "deleteFeedback", description = "Save feedback")
  public boolean deleteFeedback(@GraphQLRootContext final Context context,
      @GraphQLArgument(name = "id") final String feedbackId) {

    // TODO: Check if we are admin or the original feedback author

    providers.findAll(FeedbackDataProvider.class)
        .subscribe(d -> d.delete(feedbackId));

    return true;
  }

  @GraphQLQuery(name = "feedback", description = "Save feedback")
  public Flux<Feedback> listFeedback(
      @GraphQLRootContext final Context context,
      @GraphQLArgument(name = "offset", description = "Start offset",
          defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size", description = "Maximum values to return",
          defaultValue = "10") final int limit) {

    // TODO: Admin can list everything, user can only list there own. if not logged in then nothing

    return providers.findAll(FeedbackDataProvider.class)
        .flatMap(d -> d.findAll(offset, limit));
  }

  @GraphQLQuery(name = "pluginName", description = "Get plugin display name")
  public String pluginName(@GraphQLContext final Feedback feedback) {
    final String pluginId = feedback.getPluginId();
    Optional<String> name = Optional.empty();

    if (!StringUtils.isEmpty(pluginId)) {

      name = uiExtensions.stream()
          .filter(e -> pluginId.equalsIgnoreCase(e.getId()))
          .map(InvestUiExtension::getName)
          .findFirst();

    }

    return name.orElse(null);
  }
}
