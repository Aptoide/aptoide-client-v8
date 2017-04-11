/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.interfaces.LifecycleSchim;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by neuro on 18-04-2016.
 */
public class Displayables implements LifecycleSchim {

  private final Queue<Displayable> temporaryDisplayables;
  private final List<Displayable> displayables;

  public Displayables() {
    temporaryDisplayables = new LinkedList<>();
    displayables = new LinkedList<>();
  }

  public void add(int position, List<? extends Displayable> displayables) {
    Collections.reverse(displayables);

    for (Displayable displayable : displayables) {
      add(position, displayable);
    }
  }

  public void add(int position, Displayable displayable) {
    if (shouldIgnore(displayable)) return;

    if (displayable instanceof DisplayableGroup) {
      addDisplayableGroup(position, (DisplayableGroup) displayable);
    } else {
      displayables.add(position, displayable);
    }
  }

  private boolean shouldIgnore(Displayable displayable) {
    return displayable instanceof EmptyDisplayable;
  }

  /**
   * Uses a breadth-first-search to reach all leaf nodes transversing the list in width.
   */
  private void addDisplayableGroup(int position, DisplayableGroup displayable) {
    temporaryDisplayables.clear();
    temporaryDisplayables.addAll(displayable.getChildren());
    LinkedList<Displayable> temp = new LinkedList<>();
    while (!temporaryDisplayables.isEmpty()) {
      Displayable innerDisplayable = temporaryDisplayables.poll();
      if (innerDisplayable instanceof DisplayableGroup) {
        temporaryDisplayables.addAll(((DisplayableGroup) innerDisplayable).getChildren());
      } else {
        temp.add(innerDisplayable);
      }
    }
    Collections.reverse(temp);
    displayables.addAll(position, temp);
  }

  public void add(List<? extends Displayable> displayables) {
    for (Displayable displayable : displayables) {
      add(displayable);
    }
  }

  public void add(Displayable displayable) {
    if (shouldIgnore(displayable)) return;

    if (displayable instanceof DisplayableGroup) {
      addDisplayableGroup((DisplayableGroup) displayable);
    } else {
      displayables.add(displayable);
    }
  }

  /**
   * Uses a breadth-first-search to reach all leaf nodes transversing the list in width.
   */
  private void addDisplayableGroup(DisplayableGroup displayable) {
    temporaryDisplayables.clear();
    temporaryDisplayables.addAll(displayable.getChildren());
    while (!temporaryDisplayables.isEmpty()) {
      Displayable innerDisplayable = temporaryDisplayables.poll();
      if (innerDisplayable instanceof DisplayableGroup) {
        temporaryDisplayables.addAll(((DisplayableGroup) innerDisplayable).getChildren());
      } else {
        displayables.add(innerDisplayable);
      }
    }
  }

  public Displayable pop() {
    if (displayables.size() > 0) {
      return displayables.remove(displayables.size() - 1);
    } else {
      return null;
    }
  }

  public Displayable get(Integer position) {
    if (displayables.size() > position) {
      return displayables.get(position);
    } else {
      return null;
    }
  }

  /**
   * remove displayables from <code>startPos</code> startPos until the
   * <code>endPos</code>(inclusive)
   *
   * @param startPos position of the first element to be removed
   * @param endPos position of the last element to be removed
   */
  public int remove(int startPos, int endPos) {
    if (startPos >= 0 && startPos < size() && endPos >= startPos && endPos <= size()) {
      int numberLoops = (endPos + 1) - startPos;
      if (numberLoops == 0) {
        remove(startPos);
        return 1;
      }
      for (int i = 0; i < numberLoops; i++) {
        displayables.remove(startPos);
      }
      return numberLoops;
    }
    return 0;
  }

  public int size() {
    return displayables.size();
  }

  public void remove(int pos) {
    if (pos >= 0 && pos < displayables.size()) {
      displayables.remove(pos);
    }
  }

  public void remove(Displayable displayable) {
    displayables.remove(displayable);
  }

  public int getPosition(Displayable displayable) {
    return displayables.indexOf(displayable);
  }

  public void clear() {
    displayables.clear();
  }

  //
  // LifecycleSchim interface
  //

  public void onResume() {
    for (final Displayable displayable : displayables) {
      displayable.onResume();
    }
  }

  public void onPause() {
    for (final Displayable displayable : displayables) {
      displayable.onPause();
    }
  }

  @Override public void onViewCreated() {
    for (final Displayable displayable : displayables) {
      displayable.onViewCreated();
    }
  }

  @Override public void onDestroyView() {
    for (final Displayable displayable : displayables) {
      displayable.onDestroyView();
    }
  }

  public void onSaveInstanceState(Bundle outState) {
    for (final Displayable displayable : displayables) {
      displayable.onSaveInstanceState(outState);
    }
  }

  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    for (final Displayable displayable : displayables) {
      displayable.onViewStateRestored(savedInstanceState);
    }
  }
}
