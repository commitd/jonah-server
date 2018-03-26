package io.committed.ketos.plugins.graphql.feedback;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import io.committed.invest.core.auth.InvestRoles;
import io.committed.invest.core.graphql.InvestRootContext;
import io.committed.invest.extensions.InvestUiExtension;
import io.committed.invest.extensions.annotations.GraphQLService;
import io.committed.invest.extensions.data.providers.DataProviders;
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

  private final DataProviders providers;

  private final List<InvestUiExtension> uiExtensions;

  public FeedbackGraphQlService(final DataProviders providers,
      final List<InvestUiExtension> uiExtensions) {
    this.providers = providers;
    this.uiExtensions = uiExtensions;
  }

  @GraphQLMutation(name = "addFeedback", description = "Save feedback")
  public Mono<Feedback> addFeedback(@GraphQLRootContext final InvestRootContext context,
      @GraphQLArgument(name = "pluginId") final String pluginId,
      @GraphQLArgument(name = "subject") final String subject,
      @GraphQLArgument(name = "type") final String type,
      @GraphQLNonNull @GraphQLArgument(name = "comment") final String comment) {

    final String user = getUsername(context).orElse("guest");

    final Feedback f = Feedback.builder().comment(comment).subject(subject).type(type).user(user)
        .pluginId(pluginId).timestamp(Instant.now()).build();

    // Save into every feeback provider...
    final Feedback blockLast = getFeedbackProviders()
        .flatMap(d -> d.save(f))
        .blockLast();

    return Mono.justOrEmpty(blockLast);
  }


  @GraphQLMutation(name = "deleteFeedback", description = "Save feedback")
  public boolean deleteFeedback(@GraphQLRootContext final InvestRootContext context,
      @GraphQLArgument(name = "id") final String feedbackId) {

    final Optional<Authentication> optional = getUser(context);
    if (!optional.isPresent()) {
      return false;
    }

    final Authentication auth = optional.get();

    if (isAdmin(auth)) {
      getFeedbackProviders()
          .subscribe(d -> d.delete(feedbackId));
    } else {
      getFeedbackProviders()
          .subscribe(d -> d.deleteByUser(feedbackId, auth.getName()));
    }

    return true;
  }



  @GraphQLQuery(name = "feedback", description = "List feedback")
  public Flux<Feedback> listFeedback(@GraphQLRootContext final InvestRootContext context,
      @GraphQLArgument(name = "offset", description = "Start offset",
          defaultValue = "0") final int offset,
      @GraphQLArgument(name = "size", description = "Maximum values to return",
          defaultValue = "10") final int limit) {

    // Admin can list everything, user can only list there own. if not logged in then nothing

    final Optional<Authentication> optional = getUser(context);
    if (!optional.isPresent()) {
      return Flux.empty();
    }

    final Authentication auth = optional.get();

    if (isAdmin(auth)) {
      return getFeedbackProviders().flatMap(d -> d.findAll(offset, limit));
    } else {
      return getFeedbackProviders().flatMap(d -> d.findAllByUser(auth.getName(), offset, limit));
    }

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


  private Flux<FeedbackDataProvider> getFeedbackProviders() {
    return providers.findAll(FeedbackDataProvider.class);
  }

  private Optional<String> getUsername(final InvestRootContext context) {
    return getUser(context)
        .map(Authentication::getName);
  }

  private Optional<Authentication> getUser(final InvestRootContext context) {
    return context.getAuthentication()
        .filter(Authentication::isAuthenticated)
        .blockOptional();
  }

  private boolean isAdmin(final Authentication auth) {
    return auth.getAuthorities().stream()
        .anyMatch(p -> p.getAuthority().equals(InvestRoles.ROLE_ADMINISTRATOR));
  }

}
