package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.AbstractDisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.List;

/**
 * Created by neuro on 16-04-2016.
 */
@Ignore public class DisplayableGroupWithMargin extends AbstractDisplayableGroup {

  public DisplayableGroupWithMargin() {
  }

  public DisplayableGroupWithMargin(List<Displayable> children) {
    super(children);
  }

  public DisplayableGroupWithMargin(Displayable child) {
    super(child);
  }

  @Override public int getViewLayout() {
    return R.layout.displayable_group_with_margin;
  }
}
