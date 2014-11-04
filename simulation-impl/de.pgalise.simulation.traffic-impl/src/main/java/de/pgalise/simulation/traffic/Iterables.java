/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import java.util.Iterator;

/**
 * Replacement for google guava's Iterable utiliity class until CDI issues are
 * clarified (just throws javax.enterprise.inject.UnsatisfiedResolutionException
 * for instance)
 *
 * @author richter
 */
public class Iterables {

  public static <T> Iterable<T> cycle(final Iterable<T> it) {
    return new Iterable<T>() {
      Iterator<T> iter = it.iterator();

      @Override

      public Iterator<T> iterator() {
        return new Iterator<T>() {

          @Override
          public boolean hasNext() {
            return true;
          }

          @Override
          public T next() {
            if (!iter.hasNext()) {
              iter = it.iterator();
            }
            return iter.next();
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException("Not supported.");
          }
        };
      }
    };
  }

  private Iterables() {
  }
}
