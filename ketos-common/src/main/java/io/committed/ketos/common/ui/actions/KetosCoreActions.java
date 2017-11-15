package io.committed.ketos.common.ui.actions;

import java.util.Arrays;

import com.google.common.base.Joiner;

public class KetosCoreActions {
  public static final String SEPARATOR = ".";

  private static final Joiner JOINER = Joiner.on(SEPARATOR).skipNulls();

  //



  // Types

  public static final String DOCUMENT = "document";

  public static final String ENTITY = "entity";

  public static final String RELATION = "relation";

  public static final String EVENT = "event";


  // Activities

  public static final String VIEW = "view";


  // Actual actions

  public static final String DOCUMENT_VIEW = make(DOCUMENT, VIEW);

  public static final String ENTITY_VIEW = make(ENTITY, VIEW);

  public static final String RELATION_VIEW = make(RELATION, VIEW);

  public static final String EVENT_VIEW = make(EVENT, VIEW);



  public static String make(final String... parts) {
    return JOINER.join(Arrays.asList(parts));
  }

}
