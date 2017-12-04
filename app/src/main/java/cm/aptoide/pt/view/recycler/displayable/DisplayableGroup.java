package cm.aptoide.pt.view.recycler.displayable;

import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.view.recycler.widget.WidgetFactory;
import java.util.List;
import lombok.Getter;

/**
 * Created by neuro on 16-04-2016.
 */
public class DisplayableGroup extends Displayable {

  @Getter private final List<Displayable> children;
  private final WindowManager windowManager;
  private final Resources resources;

  public DisplayableGroup(List<Displayable> children, WindowManager windowManager,
      Resources resources) {
    this(children, true, windowManager, resources);
  }

  DisplayableGroup(List<Displayable> children, boolean computeLeftSpaces,
      WindowManager windowManager, Resources resources) {
    this.children = children;
    this.windowManager = windowManager;
    this.resources = resources;
    if (computeLeftSpaces) computeLeftSpaces();
  }

  private void computeLeftSpaces() {
    int columnSize = WidgetFactory.getColumnSize(resources, windowManager);
    int index = 0;

    for (Displayable displayable : children) {
      if (index + displayable.getSpanSize(windowManager, resources) > columnSize) {
        index = displayable.getSpanSize(windowManager, resources);
      } else {
        index += displayable.getSpanSize(windowManager, resources);
      }
    }

    if (index < columnSize) {
      children.add(new EmptyDisplayable(columnSize - index));
    }
  }

  @Override public int getDefaultPerLineCount() {
    throw new IllegalStateException(
        "getDefaultPerLineCount() on DisplayableGroup should not " + "be called!");
  }

  @Override protected Configs getConfig() {
    // Stub
    // Should not be used
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    throw new IllegalStateException(
        "getViewLayout() on DisplayableGroup should not be " + "called!");
  }
}
