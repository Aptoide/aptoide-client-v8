package cm.aptoide.pt.notification.view;

import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;

/**
 * Created by jdandrade on 14/11/2017.
 */

public class InboxNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final TabNavigator tabNavigator;

  public InboxNavigator(FragmentNavigator fragmentNavigator, TabNavigator tabNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.tabNavigator = tabNavigator;
  }

  public void navigateToTimelineWithPostId(String postId) {
    tabNavigator.navigate(new AppsTimelineTabNavigation(postId));
  }
}
