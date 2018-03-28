package io.committed.ketos.common;


import static org.mockito.Mockito.mock;
import org.junit.Test;
import io.committed.invest.test.BeanTestSupport;
import io.committed.invest.test.LombokDataTestSupport;
import io.committed.ketos.common.baleenconsumer.OutputRelation;
import io.committed.ketos.common.data.BaleenCorpus;
import io.committed.ketos.common.data.general.NamedGeoLocation;
import io.committed.ketos.common.graphql.input.DocumentFilter;
import io.committed.ketos.common.graphql.intermediate.EntitySearchResult;
import io.committed.ketos.common.graphql.output.Documents;
import io.committed.ketos.common.graphql.support.GraphQLNode;
import io.committed.ketos.common.references.BaleenDocumentReference;

public class LombokTest {

  @Test
  public void testLombok() {
    final LombokDataTestSupport mt = new LombokDataTestSupport();

    BeanTestSupport.addFactory(GraphQLNode.class, () -> mock(GraphQLNode.class));

    mt.testPackage(OutputRelation.class);

    mt.testPackage(BaleenCorpus.class);

    mt.testPackage(DocumentFilter.class);

    mt.testPackage(EntitySearchResult.class);

    mt.testPackage(Documents.class);

    mt.testPackage(BaleenDocumentReference.class);

    mt.testClass(NamedGeoLocation.class);
  }

}
