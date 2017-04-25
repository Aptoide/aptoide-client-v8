/*
 * Copyright (c) 2016.
 * Modified on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.interfaces;

/**
 * Created on 04/07/16.
 * <p>
 * Allows the component to add the behaviour of scrolling to a specific position: first or last
 * using {@link Scrollable.Position}.
 * </p>
 */
public interface Scrollable {

  void scroll(Position position);

  void itemAdded(int pos);

  void itemRemoved(int pos);

  void itemChanged(int pos);

  enum Position {
    FIRST, LAST
  }
}
