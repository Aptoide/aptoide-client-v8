package cm.aptoide.pt.v8engine.configuration.implementation;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.fragment.CommentListFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.AppsTimelineFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.CreateUserFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.DescriptionFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.DownloadsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.ExcludedUpdatesFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.FragmentTopStores;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.LatestReviewsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.MyStoresFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.OtherVersionsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.RollbackFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.ScheduledDownloadsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.ScreenshotsViewerFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SearchFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SearchPagerTabFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SendFeedbackFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SettingsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SocialFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.TimeLineFollowFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.UpdatesFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.GetAdsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.GetStoreFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.GetStoreWidgetsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.ListAppsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.ListReviewsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.ListStoresFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.MyStoresSubscribedFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.storetab.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.viewRateAndCommentReviews.RateAndReviewsFragment;
import java.util.ArrayList;

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

  @Override public Fragment newStoreFragment(String storeName) {
    return StoreFragment.newInstance(storeName);
  }

  @Override
  public Fragment newStoreFragment(String storeName, StoreContext storeContext, String storeTheme) {
    return StoreFragment.newInstance(storeName, storeContext, storeTheme);
  }

  @Override public Fragment newStoreFragment(String storeName, StoreContext storeContext) {
    return StoreFragment.newInstance(storeName, storeContext);
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
  public Fragment newStoreTabGridRecyclerFragment(Event event, String storeTheme, String tag) {
    return StoreTabGridRecyclerFragment.newInstance(event, storeTheme, tag);
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag) {
    return StoreTabGridRecyclerFragment.newInstance(event, title, storeTheme, tag);
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

  @Override public Fragment newAppsTimelineFragment(String action, String storeTheme) {
    return AppsTimelineFragment.newInstance(action, storeTheme);
  }

  @Override
  public Fragment newSubscribedStoresFragment(Event event, String label, String storeTheme,
      String tag) {
    return MyStoresFragment.newInstance(event, label, storeTheme, tag);
  }

  @Override public Fragment newSearchPagerTabFragment(String query, boolean subscribedStores,
      boolean hasMultipleFragments) {
    return SearchPagerTabFragment.newInstance(query, subscribedStores, hasMultipleFragments);
  }

  @Override public Fragment newSearchPagerTabFragment(String query, String storeName) {
    return SearchPagerTabFragment.newInstance(query, storeName);
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

  @Override public Fragment newCreateUserFragment() {
    return CreateUserFragment.newInstance();
  }

  @Override public Fragment newTimeLineFollowStatsFragment(
      TimeLineFollowFragment.FollowFragmentOpenMode openMode, long followNumber,
      String storeTheme) {
    return TimeLineFollowFragment.newInstance(openMode, followNumber, storeTheme);
  }

  @Override
  public Fragment newCommentGridRecyclerFragment(CommentType commentType, String elementId) {
    return CommentListFragment.newInstance(commentType, elementId);
  }

  @Override public Fragment newCommentGridRecyclerFragmentUrl(CommentType commentType, String url) {
    return CommentListFragment.newInstanceUrl(commentType, url);
  }
}
