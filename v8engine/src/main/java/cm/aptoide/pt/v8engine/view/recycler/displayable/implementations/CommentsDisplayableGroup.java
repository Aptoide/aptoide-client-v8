package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import cm.aptoide.pt.annotation.Ignore;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.AbstractDisplayableGroup;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.List;

/**
 * Created by neuro on 16-04-2016.
 */
@Ignore public class CommentsDisplayableGroup extends AbstractDisplayableGroup {

  public CommentsDisplayableGroup() {
  }

  public CommentsDisplayableGroup(List<Displayable> children) {
    super(children);
  }

  public CommentsDisplayableGroup(Displayable child) {
    super(child);
  }

  @Override public int getViewLayout() {
    return R.layout.comments_displayable_group;
  }
}
