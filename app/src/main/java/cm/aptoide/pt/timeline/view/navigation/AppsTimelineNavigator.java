package cm.aptoide.pt.timeline.view.navigation;

import cm.aptoide.pt.V8Engine;
import cm.aptoide.pt.view.navigator.FragmentNavigator;

/**
 * Created by marcelobenites on 16/06/17.
 */

public class AppsTimelineNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final String title;

  public AppsTimelineNavigator(FragmentNavigator fragmentNavigator, String likesTitle) {
    this.fragmentNavigator = fragmentNavigator;
    this.title = likesTitle;
  }

  public void navigateToLikesView(String cardId, long numberOfLikes) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newTimeLineLikesFragment(cardId, numberOfLikes, "default", title));
  }
}
