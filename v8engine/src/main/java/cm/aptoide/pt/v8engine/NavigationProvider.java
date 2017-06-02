package cm.aptoide.pt.v8engine;

import cm.aptoide.pt.v8engine.view.navigator.ActivityNavigator;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

public interface NavigationProvider {
  ActivityNavigator getActivityNavigator();

  FragmentNavigator getFragmentNavigator();
}
