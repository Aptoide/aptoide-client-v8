package cm.aptoide.pt.social.presenter;

import cm.aptoide.pt.account.view.LoginSignUpFragment;
import cm.aptoide.pt.account.view.MyAccountFragment;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.CommentsTimelineTabNavigation;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.TabNavigation;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.social.commentslist.PostCommentsFragment;
import cm.aptoide.pt.social.data.PostCommentDataWrapper;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.timeline.post.PostFragment;
import cm.aptoide.pt.timeline.view.TimeLineLikesFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;
import cm.aptoide.pt.view.feedback.SendFeedbackFragment;
import rx.Observable;

/**
 * Created by jdandrade on 30/06/2017.
 */

public class TimelineNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final String likesTitle;
  private final TabNavigator tabNavigator;
  private final StoreContext storeContext;

  public TimelineNavigator(FragmentNavigator fragmentNavigator, String likesTitle,
      TabNavigator tabNavigator, StoreContext storeContext) {
    this.fragmentNavigator = fragmentNavigator;
    this.likesTitle = likesTitle;
    this.tabNavigator = tabNavigator;
    this.storeContext = storeContext;
  }

  public void navigateToAppView(long appId, String packageName) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, ""),
        true);
  }

  public void navigateToAppView(String packageName) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(packageName, AppViewFragment.OpenType.OPEN_ONLY), true);
  }

  public void navigateToStoreHome(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(StoreFragment.newInstance(storeName, storeTheme), true);
  }

  public void navigateToStoreTimeline(long userId, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(userId, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome), true);
  }

  public void navigateToStoreTimeline(String storeName, String storeTheme) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(storeName, storeTheme, Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome), true);
  }

  public void navigateToAddressBook() {
    fragmentNavigator.navigateTo(AddressBookFragment.newInstance(), true);
  }

  public void navigateToLoginView() {
    fragmentNavigator.navigateTo(LoginSignUpFragment.newInstance(false, false, false), true);
  }

  public void navigateToMyAccountView() {
    fragmentNavigator.navigateTo(MyAccountFragment.newInstance(), true);
  }

  public void navigateToCommentsWithCommentDialogOpen(String cardId) {
    fragmentNavigator.navigateTo(PostCommentsFragment.newInstanceWithCommentDialog(cardId), true);
  }

  // FIXME what should happen if storeId <= 0 ?
  public void navigateToFollowersViewStore(Long storeId, String title) {
    if (storeId > 0) {
      fragmentNavigator.navigateTo(
          TimeLineFollowersFragment.newInstanceUsingStore(storeId, "DEFAULT", title, storeContext),
          true);
    }
  }

  public void navigateToFollowersViewStore(String title) {
    fragmentNavigator.navigateTo(
        TimeLineFollowersFragment.newInstanceUsingUser("DEFAULT", title, storeContext), true);
  }

  public void navigateToFollowersViewUser(Long userId, String title) {
    fragmentNavigator.navigateTo(
        TimeLineFollowersFragment.newInstanceUsingUser(userId, "DEFAULT", title, storeContext),
        true);
  }

  // FIXME what should happen if storeId <= 0 ?
  public void navigateToFollowingViewStore(Long storeId, String title) {
    if (storeId > 0) {
      fragmentNavigator.navigateTo(
          TimeLineFollowingFragment.newInstanceUsingStoreId(storeId, "DEFAULT", title,
              storeContext), true);
    }
  }

  public void navigateToFollowingViewUser(Long userId, String title) {
    fragmentNavigator.navigateTo(
        TimeLineFollowingFragment.newInstanceUsingUserId(userId, "DEFAULT", title, storeContext),
        true);
  }

  public void navigateToLikesView(String cardId, long numberOfLikes) {
    fragmentNavigator.navigateTo(
        TimeLineLikesFragment.newInstance("default", cardId, numberOfLikes, likesTitle,
            storeContext), true);
  }

  public void navigateToComments(String cardId) {
    fragmentNavigator.navigateTo(PostCommentsFragment.newInstance(cardId), true);
  }

  public Observable<String> postNavigation() {
    return tabNavigator.navigation()
        .filter(tabNavigation -> tabNavigation.getTab() == TabNavigation.TIMELINE)
        .doOnNext(tabNavigation -> tabNavigator.clearNavigation())
        .map(tabNavigation -> tabNavigation.getBundle()
            .getString(AppsTimelineTabNavigation.CARD_ID_KEY));
  }

  public Observable<PostCommentDataWrapper> commentNavigation() {
    return tabNavigator.navigation()
        .filter(tabNavigation -> tabNavigation.getTab() == TabNavigation.COMMENTS)
        .doOnNext(tabNavigation -> tabNavigator.clearNavigation())
        .map(tabNavigation -> new PostCommentDataWrapper(tabNavigation.getBundle()
            .getString(CommentsTimelineTabNavigation.POST_ID), tabNavigation.getBundle()
            .getString(CommentsTimelineTabNavigation.COMMENT_KEY), tabNavigation.getBundle()
            .getBoolean(CommentsTimelineTabNavigation.ERROR_STATUS)));
  }

  public void navigateToNotificationCenter() {
    fragmentNavigator.navigateTo(new InboxFragment(), true);
  }

  public void navigateToCreatePost() {
    fragmentNavigator.navigateTo(PostFragment.newInstanceFromTimeline(), true);
  }

  public void navigateToFeedbackScreen(String path, String postId) {
    fragmentNavigator.navigateTo(SendFeedbackFragment.newInstance(path, postId), true);
  }

  public void navigateToUserHome(long userId) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(userId, "DEFAULT", Event.Name.getUserTimeline,
            StoreFragment.OpenType.GetHome), true);
  }
}
