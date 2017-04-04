package cm.aptoide.pt.v8engine.view.configuration.implementation;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.view.addressbook.AddressBookFragment;
import cm.aptoide.pt.v8engine.view.addressbook.ThankYouConnectingFragment;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.presenter.InviteFriendsContract;
import cm.aptoide.pt.v8engine.view.addressbook.InviteFriendsFragment;
import cm.aptoide.pt.v8engine.view.addressbook.PhoneInputFragment;
import cm.aptoide.pt.v8engine.view.addressbook.SyncResultFragment;
import cm.aptoide.pt.v8engine.view.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.view.reviews.RateAndReviewsFragment;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.timeline.AppsTimelineFragment;
import cm.aptoide.pt.v8engine.view.comments.CommentListFragment;
import cm.aptoide.pt.v8engine.view.fragment.DescriptionFragment;
import cm.aptoide.pt.v8engine.view.downloads.DownloadsFragmentMvp;
import cm.aptoide.pt.v8engine.view.updates.excluded.ExcludedUpdatesFragment;
import cm.aptoide.pt.v8engine.view.store.FragmentTopStores;
import cm.aptoide.pt.v8engine.view.store.home.HomeFragment;
import cm.aptoide.pt.v8engine.view.reviews.LatestReviewsFragment;
import cm.aptoide.pt.v8engine.view.store.my.MyStoresFragment;
import cm.aptoide.pt.v8engine.view.app.OtherVersionsFragment;
import cm.aptoide.pt.v8engine.view.updates.rollback.RollbackFragment;
import cm.aptoide.pt.v8engine.view.downloads.scheduled.ScheduledDownloadsFragment;
import cm.aptoide.pt.v8engine.view.app.screenshots.ScreenshotsViewerFragment;
import cm.aptoide.pt.v8engine.view.search.SearchFragment;
import cm.aptoide.pt.v8engine.view.search.SearchPagerTabFragment;
import cm.aptoide.pt.v8engine.view.feedback.SendFeedbackFragment;
import cm.aptoide.pt.v8engine.view.settings.SettingsFragment;
import cm.aptoide.pt.v8engine.view.timeline.SocialFragment;
import cm.aptoide.pt.v8engine.view.spotandshare.SpotSharePreviewFragment;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;
import cm.aptoide.pt.v8engine.view.timeline.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.v8engine.view.timeline.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.v8engine.view.timeline.TimeLineLikesFragment;
import cm.aptoide.pt.v8engine.view.updates.UpdatesFragment;
import cm.aptoide.pt.v8engine.view.store.ads.GetAdsFragment;
import cm.aptoide.pt.v8engine.view.store.GetStoreFragment;
import cm.aptoide.pt.v8engine.view.store.GetStoreWidgetsFragment;
import cm.aptoide.pt.v8engine.view.app.ListAppsFragment;
import cm.aptoide.pt.v8engine.view.reviews.ListReviewsFragment;
import cm.aptoide.pt.v8engine.view.store.ListStoresFragment;
import cm.aptoide.pt.v8engine.view.store.my.MyStoresSubscribedFragment;
import cm.aptoide.pt.v8engine.view.store.recommended.RecommendedStoresFragment;
import cm.aptoide.pt.v8engine.view.store.StoreTabGridRecyclerFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neuro on 10-10-2016.
 */
public class FragmentProviderImpl implements FragmentProvider {

  @Override public Fragment newScreenshotsViewerFragment(ArrayList<String> uris, int currentItem) {
    return ScreenshotsViewerFragment.newInstance(uris, currentItem);
  }

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

  @Override
  public Fragment newHomeFragment(String storeName, StoreContext storeContext, String storeTheme) {
    return HomeFragment.newInstance(storeName, storeContext, storeTheme);
  }

  @Override public Fragment newSearchFragment(String query) {
    return SearchFragment.newInstance(query);
  }

  @Override public Fragment newSearchFragment(String query, boolean onlyTrustedApps) {
    return SearchFragment.newInstance(query, onlyTrustedApps);
  }

  @Override public Fragment newSearchFragment(String query, String storeName) {
    return SearchFragment.newInstance(query, storeName);
  }

  @Override public Fragment newAppViewFragment(String packageName, String storeName,
      AppViewFragment.OpenType openType) {
    return AppViewFragment.newInstance(packageName, storeName, openType);
  }

  @Override public Fragment newAppViewFragment(String md5) {
    return AppViewFragment.newInstance(md5);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName,
      AppViewFragment.OpenType openType) {
    return AppViewFragment.newInstance(appId, packageName, openType);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName) {
    return AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName) {
    return AppViewFragment.newInstance(appId, packageName, storeTheme, storeName);
  }

  @Override public Fragment newAppViewFragment(MinimalAd minimalAd) {
    return AppViewFragment.newInstance(minimalAd);
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

  @Override public Fragment newLatestReviewsFragment(long storeId) {
    return LatestReviewsFragment.newInstance(storeId);
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String storeTheme, String tag,
      StoreContext storeContext) {
    return StoreTabGridRecyclerFragment.newInstance(event, storeTheme, tag, storeContext);
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag, StoreContext storeContext) {
    return StoreTabGridRecyclerFragment.newInstance(event, title, storeTheme, tag, storeContext);
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

  @Override public Fragment newGetStoreWidgetsFragment() {
    return new GetStoreWidgetsFragment();
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
    return AppsTimelineFragment.newInstance(action, userId, storeId, storeContext);
  }

  @Override
  public Fragment newSubscribedStoresFragment(Event event, String storeTheme, String tag) {
    return MyStoresFragment.newInstance(event, storeTheme, tag);
  }

  @Override public Fragment newSearchPagerTabFragment(String query, boolean subscribedStores,
      boolean hasMultipleFragments) {
    return SearchPagerTabFragment.newInstance(query, subscribedStores, hasMultipleFragments);
  }

  @Override public Fragment newSearchPagerTabFragment(String query, String storeName) {
    return SearchPagerTabFragment.newInstance(query, storeName);
  }

  @Override public Fragment newDownloadsFragment() {
    //return DownloadsFragment.newInstance();
    return DownloadsFragmentMvp.newInstance();
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

  @Override public Fragment newDescriptionFragment(long appId, String packageName, String storeName,
      String storeTheme) {
    return DescriptionFragment.newInstance(appId, packageName, storeName, storeTheme);
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

  @Override
  public Fragment newTimeLineFollowersUsingUserIdFragment(Long userId, long followerNumber,
      String storeTheme) {
    return TimeLineFollowersFragment.newInstanceUsingUser(userId, followerNumber, storeTheme);
  }

  @Override public Fragment newTimeLineFollowingFragmentUsingUserId(Long id, long followNumber,
      String storeTheme) {
    return TimeLineFollowingFragment.newInstanceUsingUserId(id, followNumber, storeTheme);
  }

  @Override
  public Fragment newTimeLineFollowersUsingStoreIdFragment(Long storeId, long followerNumber,
      String storeTheme) {
    return TimeLineFollowersFragment.newInstanceUsingStore(storeId, followerNumber, storeTheme);
  }

  @Override public Fragment newTimeLineFollowingFragmentUsingStoreId(Long id, long followNumber,
      String storeTheme) {
    return TimeLineFollowingFragment.newInstanceUsingStoreId(id, followNumber, storeTheme);
  }

  @Override
  public Fragment newTimeLineLikesFragment(String cardUid, long numberOfLikes, String storeTheme) {
    return TimeLineLikesFragment.newInstance(storeTheme, cardUid, numberOfLikes);
  }

  @Override
  public Fragment newCommentGridRecyclerFragment(CommentType commentType, String elementId) {
    return CommentListFragment.newInstance(commentType, elementId);
  }

  @Override public Fragment newCommentGridRecyclerFragmentUrl(CommentType commentType, String url) {
    return CommentListFragment.newInstanceUrl(commentType, url);
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

  @Override public Fragment newTimeLineFollowersFragment(long followerNumber, String storeTheme) {
    return TimeLineFollowersFragment.newInstanceUsingUser(followerNumber, storeTheme);
  }

  @Override public Fragment newRecommendedStoresFragment() {
    return new RecommendedStoresFragment();
  }
}
