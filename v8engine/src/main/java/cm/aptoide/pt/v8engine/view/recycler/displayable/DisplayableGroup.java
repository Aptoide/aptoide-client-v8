
package cm.aptoide.pt.v8engine.view.recycler.displayable;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetFactory;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
@Ignore public class DisplayableGroup extends Displayable {

  @Getter private List<Displayable> children;

  public DisplayableGroup() {
  }

  DisplayableGroup(List<Displayable> children, boolean computeLeftSpaces) {
    this.children = children;
    if (computeLeftSpaces) computeLeftSpaces();
  }

  public DisplayableGroup(List<Displayable> children) {
    this(children, true);
  }

  public DisplayableGroup(Displayable child) {
    this(new LinkedList<>(Collections.singletonList(child)), true);
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

  @Override
  public int getViewLayout() {
    return R.layout.recycler_view;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }
}
