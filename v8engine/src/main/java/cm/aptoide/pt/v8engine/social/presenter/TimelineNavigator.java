package cm.aptoide.pt.v8engine.social.presenter;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.account.LoginSignUpFragment;
import cm.aptoide.pt.v8engine.view.account.MyAccountFragment;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class TimelineNavigator implements TimelineNavigation {

  private final FragmentNavigator fragmentNavigator;
  private AptoideAccountManager accountManager;
  private String likesTitle;

  public TimelineNavigator(FragmentNavigator fragmentNavigator,
      AptoideAccountManager accountManager, String likesTitle) {
    this.fragmentNavigator = fragmentNavigator;
    this.accountManager = accountManager;
    this.likesTitle = likesTitle;
  }

  @Override
  public void navigateToAppView(long appId, String packageName, AppViewFragment.OpenType openType) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY));
  }

  @Override public void navigateToAppView(String packageName, AppViewFragment.OpenType openType) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(packageName, AppViewFragment.OpenType.OPEN_ONLY));
  }

  @Override public void navigateToStoreHome(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(
        V8Engine.getFragmentProvider().newStoreFragment(storeName, storeTheme));
  }

  @Override public void navigateToStoreTimeline(long userId, String storeTheme) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(userId, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }

  @Override public void navigateToStoreTimeline(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(storeName, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }

  @Override public void navigateToAddressBook() {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider().newAddressBookFragment());
  }

  @Override public void navigateToAccountView() {
    if (accountManager.isLoggedIn()) {
      fragmentNavigator.navigateTo(MyAccountFragment.newInstance());
    } else {
      fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, true));
    }
  }

  @Override public void navigateToCommentsWithCommentDialogOpen(String cardId) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newCommentGridRecyclerFragmentWithCommentDialogOpen(CommentType.TIMELINE, cardId));
  }

  @Override public void navigateToFollowersViewStore(Long storeId, String title) {
    if (storeId > 0) {
      fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowersUsingStoreIdFragment(storeId, "DEFAULT", title));
    } else {
      fragmentNavigator.navigateTo(
          V8Engine.getFragmentProvider().newTimeLineFollowersFragment("DEFAULT", title));
    }
  }

  @Override public void navigateToFollowersViewUser(Long userId, String title) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newTimeLineFollowersUsingUserIdFragment(userId, "DEFAULT", title));
  }

  @Override public void navigateToFollowingViewStore(Long storeId, String title) {
    if (storeId > 0) {
      fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
          .newTimeLineFollowingFragmentUsingStoreId(storeId, "DEFAULT", title));
    }
  }

  @Override public void navigateToFollowingViewUser(Long userId, String title) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newTimeLineFollowingFragmentUsingUserId(userId, "DEFAULT", title));
  }

  @Override public void navigateToLikesView(String cardId, long numberOfLikes) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newTimeLineLikesFragment(cardId, numberOfLikes, "default", likesTitle));
  }

  @Override public void navigateToComments(String cardId) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newCommentGridRecyclerFragment(CommentType.TIMELINE, cardId));
  }
}
