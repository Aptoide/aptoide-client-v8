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

  void navigateToAccountView();

  void navigateToCommentsWithCommentDialogOpen(String cardId);
}
