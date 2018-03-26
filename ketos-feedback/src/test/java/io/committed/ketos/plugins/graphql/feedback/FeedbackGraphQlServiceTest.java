package io.committed.ketos.plugins.graphql.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.WebSession;
import io.committed.invest.core.auth.InvestRoles;
import io.committed.invest.core.graphql.InvestRootContext;
import io.committed.invest.extensions.InvestUiExtension;
import io.committed.invest.extensions.data.providers.AbstractDataProvider;
import io.committed.invest.extensions.data.providers.DataProviders;
import io.committed.invest.extensions.data.providers.DatabaseConstants;
import io.committed.invest.server.data.services.DefaultDatasetProviders;
import io.committed.ketos.plugins.data.feedback.data.Feedback;
import io.committed.ketos.plugins.data.feedback.data.FeedbackDataProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class FeedbackGraphQlServiceTest {

  private final List<InvestUiExtension> uiExtensions = new ArrayList<>();
  private FeedbackGraphQlService service;
  private FeedbackDataProvider fdp;
  private InvestRootContext context;
  private Authentication auth;
  private WebSession session;

  @Before
  public void before() {
    fdp = spy(new StubFeedbackDataProvider());

    final DataProviders providers = new DefaultDatasetProviders(Arrays.asList(fdp));
    service = new FeedbackGraphQlService(providers, uiExtensions);

    auth = mock(Authentication.class);
    session = mock(WebSession.class);

    context = InvestRootContext.builder()
        .authentication(Mono.just(auth))
        .session(Mono.just(session))
        .build();
  }

  @Test
  public void testAddFeedback() {
    doReturn("user").when(auth).getName();
    doReturn(true).when(auth).isAuthenticated();

    service.addFeedback(context, "actualPlugin", "subject", "type", "comment");

    final ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
    verify(fdp).save(captor.capture());

    final Feedback value = captor.getValue();
    assertThat(value.getComment()).isEqualTo("comment");
    assertThat(value.getPluginId()).isEqualTo("actualPlugin");
    assertThat(value.getSubject()).isEqualTo("subject");
    assertThat(value.getType()).isEqualTo("type");

    assertThat(value.getUser()).isEqualTo("user");

  }

  @Test
  public void testAddFeedbackWithAnon() {
    doReturn("user").when(auth).getName();
    doReturn(false).when(auth).isAuthenticated();

    service.addFeedback(context, "actualPlugin", "subject", "type", "comment");

    final ArgumentCaptor<Feedback> captor = ArgumentCaptor.forClass(Feedback.class);
    verify(fdp).save(captor.capture());

    final Feedback value = captor.getValue();
    assertThat(value.getComment()).isEqualTo("comment");
    assertThat(value.getPluginId()).isEqualTo("actualPlugin");
    assertThat(value.getSubject()).isEqualTo("subject");
    assertThat(value.getType()).isEqualTo("type");

    // Guest user if not authenticated (properly or at all)
    assertThat(value.getUser()).isEqualTo("guest");

  }

  @Test
  public void testDeleteFeedbackWithAuth() {
    doReturn("user").when(auth).getName();
    doReturn(true).when(auth).isAuthenticated();

    service.deleteFeedback(context, "feedbackId");

    verify(fdp).deleteByUser("feedbackId", "user");
  }

  @Test
  public void testDeleteFeedbackWhenAdmin() {
    doReturn("user").when(auth).getName();
    doReturn(true).when(auth).isAuthenticated();
    doReturn(Arrays.asList(new SimpleGrantedAuthority(InvestRoles.ROLE_ADMINISTRATOR))).when(auth).getAuthorities();

    service.deleteFeedback(context, "feedbackId");

    verify(fdp).delete("feedbackId");
  }

  @Test
  public void testDeleteFeedbackWhenAnon() {
    doReturn("user").when(auth).getName();
    doReturn(false).when(auth).isAuthenticated();

    service.deleteFeedback(context, "feedbackId");

    verify(fdp, never()).delete(ArgumentMatchers.anyString());
    verify(fdp, never()).deleteByUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

  }

  @Test
  public void testListFeedbackWhenAnon() {
    final List<Feedback> list = Arrays.asList(mock(Feedback.class));
    doReturn(Flux.fromIterable(list)).when(fdp).findAll(0, 10);

    final Flux<Feedback> listFeedback = service.listFeedback(context, 0, 10);

    assertThat(listFeedback.collectList().block()).isEmpty();

  }

  @Test
  public void testListFeedbackWhenUser() {
    doReturn("user").when(auth).getName();
    doReturn(true).when(auth).isAuthenticated();

    final List<Feedback> list = Arrays.asList(mock(Feedback.class));
    doReturn(Flux.fromIterable(list)).when(fdp).findAll(0, 10);

    service.listFeedback(context, 0, 10).subscribe();

    verify(fdp).findAllByUser("user", 0, 10);


  }

  @Test
  public void testListFeedbackWhenAdmin() {
    doReturn("user").when(auth).getName();
    doReturn(true).when(auth).isAuthenticated();
    doReturn(Arrays.asList(new SimpleGrantedAuthority(InvestRoles.ROLE_ADMINISTRATOR))).when(auth).getAuthorities();

    final List<Feedback> list = Arrays.asList(mock(Feedback.class));
    doReturn(Flux.fromIterable(list)).when(fdp).findAll(0, 10);

    service.listFeedback(context, 0, 10).subscribe();

    verify(fdp).findAll(0, 10);

  }

  @Test
  public void testPluginName() {
    final InvestUiExtension extension = mock(InvestUiExtension.class);
    doReturn("pluginId").when(extension).getId();
    doReturn("name").when(extension).getName();
    uiExtensions.add(extension);

    final String pluginName = service.pluginName(Feedback.builder().pluginId("pluginId").build());

    assertThat(pluginName).isEqualTo("name");
  }


  public class StubFeedbackDataProvider extends AbstractDataProvider implements FeedbackDataProvider {

    protected StubFeedbackDataProvider() {
      super("dataset", "datasource");
    }

    @Override
    public String getDatabase() {
      return DatabaseConstants.MEMORY;
    }

    @Override
    public Flux<Feedback> findAll(final int offset, final int limit) {
      return Flux.empty();
    }

    @Override
    public Mono<Feedback> save(final Feedback feedback) {
      return Mono.empty();
    }

    @Override
    public void delete(final String id) {
      // Do nothing
    }

    @Override
    public Flux<Feedback> findAllByUser(final String user, final int offset, final int limit) {
      // Do nothing
      return Flux.empty();
    }

    @Override
    public void deleteByUser(final String id, final String user) {
      // Do nothing

    }

  }
}
