package cm.aptoide.pt.view.configuration.implementation;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.addressbook.data.Contact;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.addressbook.view.InviteFriendsFragment;
import cm.aptoide.pt.addressbook.view.PhoneInputFragment;
import cm.aptoide.pt.addressbook.view.SyncResultFragment;
import cm.aptoide.pt.addressbook.view.ThankYouConnectingFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.ListAppsFragment;
import cm.aptoide.pt.app.view.OtherVersionsFragment;
import cm.aptoide.pt.comments.view.CommentListFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.download.view.DownloadsFragment;
import cm.aptoide.pt.download.view.scheduled.ScheduledDownloadsFragment;
import cm.aptoide.pt.presenter.InviteFriendsContract;
import cm.aptoide.pt.reviews.LatestReviewsFragment;
import cm.aptoide.pt.reviews.ListReviewsFragment;
import cm.aptoide.pt.reviews.RateAndReviewsFragment;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.settings.SettingsFragment;
import cm.aptoide.pt.social.view.TimelineFragment;
import cm.aptoide.pt.spotandshare.view.SpotSharePreviewFragment;
import cm.aptoide.pt.store.view.FragmentTopStores;
import cm.aptoide.pt.store.view.GetStoreFragment;
import cm.aptoide.pt.store.view.GetStoreWidgetsFragment;
import cm.aptoide.pt.store.view.ListStoresFragment;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.store.view.ads.GetAdsFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.store.view.my.MyStoresSubscribedFragment;
import cm.aptoide.pt.store.view.recommended.RecommendedStoresFragment;
import cm.aptoide.pt.timeline.view.SocialFragment;
import cm.aptoide.pt.timeline.view.TimeLineLikesFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.updates.view.UpdatesFragment;
import cm.aptoide.pt.updates.view.excluded.ExcludedUpdatesFragment;
import cm.aptoide.pt.updates.view.rollback.RollbackFragment;
import cm.aptoide.pt.view.configuration.FragmentProvider;
import cm.aptoide.pt.view.feedback.SendFeedbackFragment;
import cm.aptoide.pt.view.fragment.DescriptionFragment;
import java.util.List;

/**
 * Created by neuro on 10-10-2016.
 */
public class VanillaFragmentProvider implements FragmentProvider {

  @Override public Fragment newSendFeedbackFragment(String screenshotFilePath) {
    return SendFeedbackFragment.newInstance(screenshotFilePath);
  }

  @Override public Fragment newStoreFragment(String storeName, String storeTheme) {
    return StoreFragment.newInstance(storeName, storeTheme);
  }

  @Override public Fragment newStoreFragment(String storeName, String storeTheme,
      StoreFragment.OpenType openType) {
    return StoreFragment.newInstance(storeName, storeTheme, openType);
  }

  @Override
  public Fragment newStoreFragment(String storeName, String storeTheme, Event.Name defaultTab,
      StoreFragment.OpenType openType) {
    return StoreFragment.newInstance(storeName, storeTheme, defaultTab, openType);
  }

  @Override public Fragment newStoreFragment(long userId, String storeTheme, Event.Name defaultTab,
      StoreFragment.OpenType openType) {
    return StoreFragment.newInstance(userId, storeTheme, defaultTab, openType);
  }

  @Override public Fragment newStoreFragment(long userId, String storeTheme,
      StoreFragment.OpenType openType) {
    return StoreFragment.newInstance(userId, storeTheme, openType);
  }

  @Override public Fragment newAppViewFragment(String packageName, String storeName,
      AppViewFragment.OpenType openType) {
    return AppViewFragment.newInstance(packageName, storeName, openType);
  }

  @Override public Fragment newAppViewFragment(String md5) {
    return AppViewFragment.newInstance(md5);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName,
      AppViewFragment.OpenType openType, String tag) {
    return AppViewFragment.newInstance(appId, packageName, openType, tag);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String tag) {
    return AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, tag);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag) {
    return AppViewFragment.newInstance(appId, packageName, storeTheme, storeName, tag);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag, String editorsBrickPosition) {
    return AppViewFragment.newInstance(appId, packageName, storeTheme, storeName, tag,
        editorsBrickPosition);
  }

  @Override public Fragment newAppViewFragment(SearchAdResult searchAdResult, String tag) {
    return AppViewFragment.newInstance(searchAdResult, tag);
  }

  @Override
  public Fragment newAppViewFragment(String packageName, AppViewFragment.OpenType openType) {
    return AppViewFragment.newInstance(packageName, openType);
  }

  @Override public Fragment newFragmentTopStores() {
    return FragmentTopStores.newInstance();
  }

  @Override public Fragment newUpdatesFragment() {
    return UpdatesFragment.newInstance();
  }

  @Override public Fragment newLatestReviewsFragment(long storeId, StoreContext storeContext) {
    return LatestReviewsFragment.newInstance(storeId, storeContext);
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String storeTheme, String tag,
      StoreContext storeContext, boolean addAdultFilter) {
    return StoreTabGridRecyclerFragment.newInstance(event, storeTheme, tag, storeContext,
        addAdultFilter);
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag, StoreContext storeContext, boolean addAdultFilter) {
    return StoreTabGridRecyclerFragment.newInstance(event, title, storeTheme, tag, storeContext,
        addAdultFilter);
  }

  @Override public Fragment newListAppsFragment() {
    return new ListAppsFragment();
  }

  @Override public Fragment newGetStoreFragment() {
    return new GetStoreFragment();
  }

  @Override public Fragment newMyStoresSubscribedFragment() {
    return new MyStoresSubscribedFragment();
  }

  @Override public Fragment newMyStoresFragment() {
    return new MyStoresFragment();
  }

  @Override public Fragment newGetStoreWidgetsFragment(boolean addAdultFilter) {
    return GetStoreWidgetsFragment.newInstance(addAdultFilter);
  }

  @Override public Fragment newListReviewsFragment() {
    return new ListReviewsFragment();
  }

  @Override public Fragment newGetAdsFragment() {
    return new GetAdsFragment();
  }

  @Override public Fragment newListStoresFragment() {
    return new ListStoresFragment();
  }

  @Override public Fragment newAppsTimelineFragment(String action, Long userId, Long storeId,
      StoreContext storeContext) {
    return TimelineFragment.newInstance(action, userId, storeId, storeContext);
  }

  @Override public Fragment newSubscribedStoresFragment(Event event, String storeTheme, String tag,
      StoreContext storeName) {
    return MyStoresFragment.newInstance(event, storeTheme, tag, storeName);
  }

  @Override public Fragment newDownloadsFragment() {
    return DownloadsFragment.newInstance();
  }

  @Override
  public Fragment newOtherVersionsFragment(String appName, String appImgUrl, String appPackage) {
    return OtherVersionsFragment.newInstance(appName, appImgUrl, appPackage);
  }

  @Override public Fragment newRollbackFragment() {
    return RollbackFragment.newInstance();
  }

  @Override public Fragment newExcludedUpdatesFragment() {
    return ExcludedUpdatesFragment.newInstance();
  }

  @Override public Fragment newScheduledDownloadsFragment() {
    return ScheduledDownloadsFragment.newInstance();
  }

  @Override
  public Fragment newScheduledDownloadsFragment(ScheduledDownloadsFragment.OpenMode openMode) {
    return ScheduledDownloadsFragment.newInstance(openMode);
  }

  @Override public Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName, String storeTheme) {
    return RateAndReviewsFragment.newInstance(appId, appName, storeName, packageName, storeTheme);
  }

  @Override public Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName, long reviewId) {
    return RateAndReviewsFragment.newInstance(appId, appName, storeName, packageName, reviewId);
  }

  @Override
  public Fragment newDescriptionFragment(String appName, String description, String storeTheme) {
    return DescriptionFragment.newInstance(appName, description, storeTheme);
  }

  @Override public Fragment newSocialFragment(String socialUrl, String pageTitle) {
    return SocialFragment.newInstance(socialUrl, pageTitle);
  }

  @Override public Fragment newSettingsFragment() {
    return SettingsFragment.newInstance();
  }

  @Override public Fragment newTimeLineFollowersUsingUserIdFragment(Long userId, String storeTheme,
      String title, StoreContext storeName) {
    return TimeLineFollowersFragment.newInstanceUsingUser(userId, storeTheme, title, storeName);
  }

  @Override
  public Fragment newTimeLineFollowingFragmentUsingUserId(Long id, String storeTheme, String title,
      StoreContext storeContext) {
    return TimeLineFollowingFragment.newInstanceUsingUserId(id, storeTheme, title, storeContext);
  }

  @Override
  public Fragment newTimeLineFollowersUsingStoreIdFragment(Long storeId, String storeTheme,
      String title, StoreContext storeContext) {
    return TimeLineFollowersFragment.newInstanceUsingStore(storeId, storeTheme, title,
        storeContext);
  }

  @Override
  public Fragment newTimeLineFollowingFragmentUsingStoreId(Long id, String storeTheme, String title,
      StoreContext storeName) {
    return TimeLineFollowingFragment.newInstanceUsingStoreId(id, storeTheme, title, storeName);
  }

  @Override
  public Fragment newTimeLineLikesFragment(String cardUid, long numberOfLikes, String storeTheme,
      String title, StoreContext storeContext) {
    return TimeLineLikesFragment.newInstance(storeTheme, cardUid, numberOfLikes, title,
        storeContext);
  }

  @Override
  public Fragment newCommentGridRecyclerFragment(CommentType commentType, String elementId,
      StoreContext storeContext) {
    return CommentListFragment.newInstance(commentType, elementId, storeContext);
  }

  @Override public Fragment newCommentGridRecyclerFragmentUrl(CommentType commentType, String url,
      String storeAnalyticsAction, StoreContext storeContext) {
    return CommentListFragment.newInstanceUrl(commentType, url, storeAnalyticsAction, storeContext);
  }

  @Override
  public Fragment newCommentGridRecyclerFragmentWithCommentDialogOpen(CommentType commentType,
      String elementId, StoreContext storeContext) {
    return CommentListFragment.newInstanceWithCommentDialogOpen(commentType, elementId,
        storeContext);
  }

  @Override public Fragment newAddressBookFragment() {
    return AddressBookFragment.newInstance();
  }

  @Override public Fragment newSyncSuccessFragment(List<Contact> contacts, String tag) {
    return SyncResultFragment.newInstance(contacts, tag);
  }

  @Override public Fragment newPhoneInputFragment(String tag) {
    return PhoneInputFragment.newInstance(tag);
  }

  @Override public Fragment newInviteFriendsFragment(InviteFriendsContract.View.OpenMode openMode,
      String tag) {
    return InviteFriendsFragment.newInstance(openMode, tag);
  }

  @Override public Fragment newSpotShareFragment(boolean showToolbar) {
    return SpotSharePreviewFragment.newInstance(showToolbar);
  }

  @Override public Fragment newThankYouConnectingFragment(String tag) {
    return ThankYouConnectingFragment.newInstance(tag);
  }

  @Override public Fragment newTimeLineFollowersFragment(String storeTheme, String title,
      StoreContext storeContext) {
    return TimeLineFollowersFragment.newInstanceUsingUser(storeTheme, title, storeContext);
  }

  @Override public Fragment newRecommendedStoresFragment() {
    return new RecommendedStoresFragment();
  }
}
