package cm.aptoide.pt.account.view;

import android.net.Uri;
import android.support.annotation.NonNull;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.link.LinksHandlerFactory;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.notification.AptoideNotification;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;
import rx.Observable;

/**
 * Created by pedroribeiro on 16/05/17.
 */

public class MyAccountNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final TabNavigator tabNavigator;
  private final LinksHandlerFactory linkFactory;

  public MyAccountNavigator(FragmentNavigator fragmentNavigator, TabNavigator tabNavigator,
      LinksHandlerFactory linkFactory) {
    this.fragmentNavigator = fragmentNavigator;
    this.tabNavigator = tabNavigator;
    this.linkFactory = linkFactory;
  }

  public void navigateToInboxView() {
    fragmentNavigator.navigateTo(new InboxFragment(), true);
  }

  public void navigateToEditStoreView(Store store) {
    ManageStoreViewModel viewModel = new ManageStoreViewModel(store.getId(), StoreTheme.fromName(
        store.getAppearance()
            .getTheme()), store.getName(), store.getAppearance()
        .getDescription(), store.getAvatar(), store.getSocialChannels());
    fragmentNavigator.navigateTo(ManageStoreFragment.newInstance(viewModel, false), true);
  }

  public void navigateToEditProfileView() {
    fragmentNavigator.navigateTo(ManageUserFragment.newInstanceToEdit(), true);
  }

  public void navigateToUserView(String userId, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(userId, storeTheme, StoreFragment.OpenType.GetHome), true);
  }

  public void navigateToStoreView(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(storeName, storeTheme, StoreFragment.OpenType.GetStore), true);
  }

  public void navigateToTimelineWithPostId(String postId) {
    tabNavigator.navigate(new AppsTimelineTabNavigation(postId));
  }

  @NonNull Observable<String> goToNotification(AptoideNotification notification,
      MyAccountView view) {
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
