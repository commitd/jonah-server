package io.committed.ketos.common.graphql.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

public class GraphQLNodeTest {

  @Test
  public void testNoParent() {
    final StubGraphQlNode stubGraphQlNode = new StubGraphQlNode(null);

    // Will find itself
    final Optional<GraphQLNode> findParent = stubGraphQlNode.findParent(GraphQLNode.class);
    assertThat(findParent.get()).isSameAs(stubGraphQlNode);

    // Will not find Another
    assertThat(stubGraphQlNode.findParent(AnotherGraphQlNode.class)).isEmpty();
  }

  @Test
  public void testHasParent() {
    final AnotherGraphQlNode anotherGraphQlNode = new AnotherGraphQlNode(null);
    final StubGraphQlNode stubGraphQlNode = new StubGraphQlNode(anotherGraphQlNode);

    // Will find itself
    final Optional<AnotherGraphQlNode> findParent =
        stubGraphQlNode.findParent(AnotherGraphQlNode.class);
    assertThat(findParent.get()).isSameAs(anotherGraphQlNode);

    // Will not find grandparent
    assertThat(stubGraphQlNode.findParent(GrandParentGraphQlNode.class)).isEmpty();
  }

  @Test
  public void testHasGrandParent() {
    final GrandParentGraphQlNode grandGraphQlNode = new GrandParentGraphQlNode(null);

    final AnotherGraphQlNode anotherGraphQlNode = new AnotherGraphQlNode(grandGraphQlNode);
    final StubGraphQlNode stubGraphQlNode = new StubGraphQlNode(anotherGraphQlNode);

    // Will find itself
    final Optional<AnotherGraphQlNode> findParent =
        stubGraphQlNode.findParent(AnotherGraphQlNode.class);
    assertThat(findParent.get()).isSameAs(anotherGraphQlNode);

    // Will not find Another
    final Optional<GrandParentGraphQlNode> findGrand =
        stubGraphQlNode.findParent(GrandParentGraphQlNode.class);
    assertThat(findGrand.get()).isSameAs(grandGraphQlNode);
  }

  public static class StubGraphQlNode extends AbstractGraphQLNode {

    public StubGraphQlNode(final GraphQLNode parent) {
      super(parent);
    }
  }

  public static class AnotherGraphQlNode extends AbstractGraphQLNode {

    public AnotherGraphQlNode(final GraphQLNode parent) {
      super(parent);
    }
  }

  public static class GrandParentGraphQlNode extends AbstractGraphQLNode {

    public GrandParentGraphQlNode(final GraphQLNode parent) {
      super(parent);
    }
  }
}
