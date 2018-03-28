package io.committed.ketos.common.baleenconsumer;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.committed.ketos.common.data.BaleenDocument;
import io.committed.ketos.common.data.BaleenEntity;
import io.committed.ketos.common.data.BaleenMention;
import io.committed.ketos.common.data.BaleenRelation;

public class ConvertersTest {

  @Test
  public void testToBaleenDocument() {
    final OutputDocument input = new OutputDocument();
    input.setContent("test");
    input.setExternalId("externalId");
    input.setMetadata(Arrays.asList(new OutputDocumentMetadata("k1", "v2")));
    final Map<String, Object> map = new HashMap<>();
    map.put("p", "pv");
    input.setProperties(map);

    final BaleenDocument baleenDocument = Converters.toBaleenDocument(input);
    final OutputDocument output = Converters.toOutputDocument(baleenDocument);

    assertEquals(input, output);
  }

  @Test
  public void testToBaleenEntity() {
    final OutputEntity input = new OutputEntity();

    input.setMentionIds(null);
    input.setDocId("dd");
    final Map<String, Object> properties = new HashMap<>();
    properties.put("p", "pv");
    input.setProperties(properties);
    input.setSubType("svdv");
    input.setType("32555");
    input.setValue("ccc");

    final BaleenEntity mid = Converters.toBaleenEntity(input);
    final OutputEntity output = Converters.toOutputEntity(mid);
    assertEquals(input, output);
  }

  @Test
  public void testToBaleenMention() {
    final OutputMention input = new OutputMention();
    input.setBegin(24);
    input.setEnd(435);
    input.setEntityId("asd");
    input.setDocId("dd");
    final Map<String, Object> properties = new HashMap<>();
    properties.put("p", "pv");
    input.setProperties(properties);
    input.setSubType("svdv");
    input.setType("32555");
    input.setValue("ccc");

    final BaleenMention mid = Converters.toBaleenMention(input);
    final OutputMention output = Converters.toOutputMention(mid);
    assertEquals(input, output);
  }

  @Test
  public void testToBaleenRelation() {
    final OutputRelation input = new OutputRelation();

    input.setBegin(3);
    input.setDocId("dsds");
    input.setEnd(84);
    input.setExternalId("asda");
    final Map<String, Object> properties = new HashMap<>();
    properties.put("p", "pv");
    input.setProperties(properties);
    input.setSubType("st");
    input.setType("tt");

    input.setSource(createMentionForRelation("s"));
    input.setTarget(createMentionForRelation("t"));

    final BaleenRelation mid = Converters.toBaleenRelation(input);
    final OutputRelation output = Converters.toOutputRelation(mid);
    assertEquals(input, output);
  }

  private OutputMention createMentionForRelation(final String prefix) {
    final OutputMention input = new OutputMention();
    input.setBegin(24);
    input.setEnd(435);
    input.setEntityId(prefix + "asd");
    input.setDocId("dd");
    final Map<String, Object> properties = new HashMap<>();
    properties.put(prefix + "p", "pv");
    input.setProperties(properties);
    input.setSubType(prefix + "svdv");
    input.setType(prefix + "32555");
    input.setValue(prefix + "ccc");

    return input;
  }
}
