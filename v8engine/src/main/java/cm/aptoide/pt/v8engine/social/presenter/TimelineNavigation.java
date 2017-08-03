package cm.aptoide.pt.v8engine.social.presenter;

import cm.aptoide.pt.v8engine.view.app.AppViewFragment;

/**
 * Created by jdandrade on 30/06/2017.
 */

public interface TimelineNavigation {

  void navigateToAppView(long appId, String packageName, AppViewFragment.OpenType openType);

  void navigateToAppView(String packageName, AppViewFragment.OpenType openType);

  void navigateToStoreHome(String storeName, String storeTheme);

  void navigateToStoreTimeline(long userId, String storeTheme);

  void navigateToStoreTimeline(String storeName, String storeTheme);

  void navigateToAddressBook();

  void navigateToLoginView();

  void navigateToMyAccountView();

  void navigateToCommentsWithCommentDialogOpen(String cardId);

  void navigateToFollowersViewStore(Long storeId, String title);

  void navigateToFollowersViewStore(String title);

  void navigateToFollowersViewUser(Long userId, String title);

  void navigateToFollowingViewStore(Long storeId, String title);

  void navigateToFollowingViewUser(Long userId, String title);

  void navigateToLikesView(String cardId, long numberOfLikes);

  void navigateToComments(String cardId);

  rx.Observable<String> postNavigation();
}
