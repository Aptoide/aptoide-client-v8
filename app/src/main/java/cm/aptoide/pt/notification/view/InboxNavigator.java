package cm.aptoide.pt.notification.view;

import android.net.Uri;
import android.support.annotation.NonNull;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;
import rx.Observable;

/**
 * Created by jdandrade on 14/11/2017.
 */

public class InboxNavigator {
  private final TabNavigator tabNavigator;
  private final LinksHandlerFactory linkFactory;

  public InboxNavigator(TabNavigator tabNavigator, LinksHandlerFactory linkFactory) {
    this.tabNavigator = tabNavigator;
    this.linkFactory = linkFactory;
  }

  public void navigateToTimelineWithPostId(String postId) {
    tabNavigator.navigate(new AppsTimelineTabNavigation(postId));
  }

  @NonNull Observable<String> navigateToNotification(AptoideNotification notification,
      InboxView view) {
    return Observable.just(
        linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK, notification.getUrl()))
        .flatMap(link -> {
          String cardId = Uri.parse(link.getUrl())
              .getQueryParameter("cardId");
          if (cardId != null) {
            return Observable.just(cardId);
          } else {
            return Observable.empty();
          }
        })
        .doOnNext(postId -> {
          if (postId != null) {
            navigateToTimelineWithPostId(postId);
            view.goHome();
          } else {
            linkFactory.get(LinksHandlerFactory.NOTIFICATION_LINK, notification.getUrl())
                .launch();
          }
        });
  }
}
