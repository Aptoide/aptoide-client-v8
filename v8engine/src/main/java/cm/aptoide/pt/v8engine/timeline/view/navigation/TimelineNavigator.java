package cm.aptoide.pt.v8engine.timeline.view.navigation;

import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;

/**
 * Created by marcelobenites on 16/06/17.
 */

public class TimelineNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final String title;

  public TimelineNavigator(FragmentNavigator fragmentNavigator, String likesTitle) {
    this.fragmentNavigator = fragmentNavigator;
    this.title = likesTitle;
  }

  public void navigateToLikesView(String cardId, long numberOfLikes) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newTimeLineLikesFragment(cardId, numberOfLikes, "default", title));
  }
 }
