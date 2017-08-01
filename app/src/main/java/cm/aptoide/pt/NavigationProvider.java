package cm.aptoide.pt;

import cm.aptoide.pt.view.navigator.ActivityNavigator;
import cm.aptoide.pt.view.navigator.FragmentNavigator;

public interface NavigationProvider {
  ActivityNavigator getActivityNavigator();

  FragmentNavigator getFragmentNavigator();
}
