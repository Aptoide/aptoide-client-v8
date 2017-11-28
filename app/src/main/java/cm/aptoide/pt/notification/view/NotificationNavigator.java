package cm.aptoide.pt.notification.view;

import android.net.Uri;
import cm.aptoide.pt.link.Link;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;

public class NotificationNavigator {

  private final TabNavigator tabNavigator;
  private final LinksHandlerFactory linkFactory;
  private final FragmentNavigator fragmentNavigator;

  public NotificationNavigator(TabNavigator tabNavigator, LinksHandlerFactory linkFactory,
      FragmentNavigator fragmentNavigator) {
    this.tabNavigator = tabNavigator;
    this.linkFactory = linkFactory;
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToNotification(AptoideNotification notification) {

    final Link link = linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK, notification.getUrl());
    final String postId = Uri.parse(link.getUrl())
        .getQueryParameter("cardId");

    if (postId != null) {
      tabNavigator.navigate(new AppsTimelineTabNavigation(postId));
      fragmentNavigator.cleanBackStack();
    } else {
      link.launch();
    }
  }
}