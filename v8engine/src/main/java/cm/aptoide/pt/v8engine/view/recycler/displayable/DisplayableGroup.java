/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;
import java.util.List;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
@Ignore public class DisplayableGroup extends Displayable {

  @Getter private final List<Displayable> children;

  public DisplayableGroup(List<Displayable> children, boolean computeLeftSpaces) {
    this.children = children;
    if (computeLeftSpaces) computeLeftSpaces();
  }

  public DisplayableGroup(List<Displayable> children) {
    this(children, true);
  }

  private void computeLeftSpaces() {
    int columnSize = WidgetFactory.getColumnSize();
    int index = 0;

    for (Displayable displayable : children) {
      if (index + displayable.getSpanSize() > columnSize) {
        index = displayable.getSpanSize();
      } else {
        index += displayable.getSpanSize();
      }
    }

    if (index < columnSize) {
      children.add(new EmptyDisplayable(columnSize - index));
    }
  }

  @Override public Type getType() {
    throw new IllegalStateException("getType() on DisplayableGroup should not be called!");
  }

  @Override public int getViewLayout() {
    throw new IllegalStateException(
        "getViewLayout() on DisplayableGroup should not be " + "called!");
  }

  @Override public int getDefaultPerLineCount() {
    throw new IllegalStateException(
        "getDefaultPerLineCount() on DisplayableGroup should not " + "be called!");
  }
}
