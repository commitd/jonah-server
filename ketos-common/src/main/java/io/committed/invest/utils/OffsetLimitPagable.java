package io.committed.invest.utils;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetLimitPagable implements Pageable {

  private final long offset;
  private final int limit;

  public OffsetLimitPagable(final long offset, final int limit) {
    this.offset = offset;
    this.limit = limit;

  }

  @Override
  public int getPageNumber() {
    return (int) (offset / limit);
  }

  @Override
  public int getPageSize() {
    return limit;
  }

  @Override
  public long getOffset() {
    return offset;
  }

  @Override
  public Sort getSort() {
    return Sort.unsorted();
  }

  @Override
  public Pageable next() {
    return new OffsetLimitPagable(offset + limit, limit);
  }

  @Override
  public Pageable previousOrFirst() {
    return new OffsetLimitPagable(Math.max(offset - limit, 0), limit);
  }

  @Override
  public Pageable first() {
    return new OffsetLimitPagable(0, limit);
  }

  @Override
  public boolean hasPrevious() {
    return offset > 0;
  }



}
