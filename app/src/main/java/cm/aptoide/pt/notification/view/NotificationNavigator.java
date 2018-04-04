package cm.aptoide.pt.notification.view;

import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigator;

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
}