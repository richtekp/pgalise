package org.khelekore.prtree;

import java.util.Comparator;

class DataComparators<T> implements NodeComparators<T> {

  private static final long serialVersionUID = 7952899091677429406L;
  private final MBRConverter<T> converter;

  public DataComparators(MBRConverter<T> converter) {
    this.converter = converter;
  }

  @Override
  public Comparator<T> getMinComparator(final int axis) {
    return new Comparator<T>() {
      @Override
      public int compare(T t1, T t2) {
        double d1 = converter.getMin(axis, t1);
        double d2 = converter.getMin(axis, t2);
        return Double.compare(d1, d2);
      }
    };
  }

  @Override
  public Comparator<T> getMaxComparator(final int axis) {
    return new Comparator<T>() {
      @Override
      public int compare(T t1, T t2) {
        double d1 = converter.getMax(axis, t1);
        double d2 = converter.getMax(axis, t2);
        return Double.compare(d1, d2);
      }
    };
  }
}
