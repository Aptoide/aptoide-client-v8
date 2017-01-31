package cm.aptoide.pt.v8engine.interfaces;

import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import java.util.List;

public interface DisplayableManager {

  DisplayableManager addDisplayable(int position, Displayable displayable, boolean finishedLoading);

  DisplayableManager addDisplayable(Displayable displayable, boolean finishedLoading);

  DisplayableManager addDisplayables(List<? extends Displayable> displayables,
      boolean finishedLoading);

  @Deprecated DisplayableManager addDisplayables(int position,
      List<? extends Displayable> displayables, boolean finishedLoading);

  DisplayableManager clearDisplayables();
}
