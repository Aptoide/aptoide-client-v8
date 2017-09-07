package cm.aptoide.pt.social.presenter;

import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;
import cm.aptoide.pt.view.account.LoginSignUpFragment;
import cm.aptoide.pt.view.account.MyAccountFragment;
import cm.aptoide.pt.view.app.AppViewFragment;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.navigator.TabNavigation;
import cm.aptoide.pt.view.navigator.TabNavigator;
import cm.aptoide.pt.view.store.StoreFragment;
import rx.Observable;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class TimelineNavigator implements TimelineNavigation {

  private final FragmentNavigator fragmentNavigator;
  private final String likesTitle;
  private final TabNavigator tabNavigator;

  public TimelineNavigator(FragmentNavigator fragmentNavigator, String likesTitle,
      TabNavigator tabNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.likesTitle = likesTitle;
    this.tabNavigator = tabNavigator;
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
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newStoreFragment(storeName, storeTheme));
  }

  @Override public void navigateToStoreTimeline(long userId, String storeTheme) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newStoreFragment(userId, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }

  @Override public void navigateToStoreTimeline(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newStoreFragment(storeName, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome));
  }

  @Override public void navigateToAddressBook() {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newAddressBookFragment());
  }

  @Override public void navigateToLoginView() {
    fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false));
  }

  @Override public void navigateToMyAccountView() {
    fragmentNavigator.navigateTo(MyAccountFragment.newInstance());
  }

  @Override public void navigateToCommentsWithCommentDialogOpen(String cardId) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newCommentGridRecyclerFragmentWithCommentDialogOpen(CommentType.TIMELINE, cardId));
  }

  // FIXME what should happen if storeId <= 0 ?
  @Override public void navigateToFollowersViewStore(Long storeId, String title) {
    if (storeId > 0) {
      fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
          .newTimeLineFollowersUsingStoreIdFragment(storeId, "DEFAULT", title));
    }
  }

  @Override public void navigateToFollowersViewStore(String title) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newTimeLineFollowersFragment("DEFAULT", title));
  }

  @Override public void navigateToFollowersViewUser(Long userId, String title) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newTimeLineFollowersUsingUserIdFragment(userId, "DEFAULT", title));
  }

  // FIXME what should happen if storeId <= 0 ?
  @Override public void navigateToFollowingViewStore(Long storeId, String title) {
    if (storeId > 0) {
      fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
          .newTimeLineFollowingFragmentUsingStoreId(storeId, "DEFAULT", title));
    }
  }

  @Override public void navigateToFollowingViewUser(Long userId, String title) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newTimeLineFollowingFragmentUsingUserId(userId, "DEFAULT", title));
  }

  @Override public void navigateToLikesView(String cardId, long numberOfLikes) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newTimeLineLikesFragment(cardId, numberOfLikes, "default", likesTitle));
  }

  @Override public void navigateToComments(String cardId) {
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newCommentGridRecyclerFragment(CommentType.TIMELINE, cardId));
  }

  @Override public Observable<String> postNavigation() {
    return tabNavigator.navigation()
        .filter(tabNavigation -> tabNavigation.getTab() == TabNavigation.TIMELINE)
        .doOnNext(tabNavigation -> tabNavigator.clearNavigation())
        .map(tabNavigation -> tabNavigation.getBundle()
            .getString(AppsTimelineTabNavigation.CARD_ID_KEY));
  }

  @Override public void navigateToNotificationCenter() {
    fragmentNavigator.navigateTo(new InboxFragment());
  }
}
