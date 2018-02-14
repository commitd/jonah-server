package io.committed.ketos.common.baleenconsumer;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class OutputFullDocument extends OutputDocument {

  private Collection<OutputMention> mentions;

  private Collection<OutputEntity> entities;

  private Collection<OutputRelation> relations;

  private Set<Date> dates;

  private Set<OutputLatLon> pois;

}
