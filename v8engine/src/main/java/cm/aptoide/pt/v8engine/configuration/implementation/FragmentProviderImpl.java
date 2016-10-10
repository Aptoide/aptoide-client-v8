package cm.aptoide.pt.v8engine.configuration.implementation;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.configuration.FragmentProvider;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.ScheduledDownloadsFragment;
import java.util.ArrayList;

/**
 * Created by neuro on 10-10-2016.
 */
public class FragmentProviderImpl implements FragmentProvider {

  @Override public Fragment newScreenshotsViewerFragment(ArrayList<String> uris, int currentItem) {
    return null;
  }

  @Override public Fragment newSendFeedbackFragment(String screenshotFilePath) {
    return null;
  }

  @Override public Fragment newStoreFragment(String storeName, String storeTheme) {
    return null;
  }

  @Override public Fragment newStoreFragment(String storeName) {
    return null;
  }

  @Override
  public Fragment newStoreFragment(String storeName, StoreContext storeContext, String storeTheme) {
    return null;
  }

  @Override public Fragment newStoreFragment(String storeName, StoreContext storeContext) {
    return null;
  }

  @Override
  public Fragment newHomeFragment(String storeName, StoreContext storeContext, String storeTheme) {
    return null;
  }

  @Override public Fragment newSearchFragment(String query) {
    return null;
  }

  @Override public Fragment newSearchFragment(String query, boolean onlyTrustedApps) {
    return null;
  }

  @Override public Fragment newSearchFragment(String query, String storeName) {
    return null;
  }

  @Override public Fragment newAppViewFragment(String packageName, String storeName,
      AppViewFragment.OpenType openType) {
    return null;
  }

  @Override public Fragment newAppViewFragment(String md5) {
    return null;
  }

  @Override public Fragment newAppViewFragment(long appId) {
    return null;
  }

  @Override public Fragment newAppViewFragment(long appId, String storeTheme, String storeName) {
    return null;
  }

  @Override public Fragment newAppViewFragment(MinimalAd minimalAd) {
    return null;
  }

  @Override public Fragment newAppViewFragment(GetAdsResponse.Ad ad) {
    return null;
  }

  @Override
  public Fragment newAppViewFragment(String packageName, AppViewFragment.OpenType openType) {
    return null;
  }

  @Override public Fragment newFragmentTopStores() {
    return null;
  }

  @Override public Fragment newUpdatesFragment() {
    return null;
  }

  @Override public Fragment newLatestReviewsFragment(long storeId) {
    return null;
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag) {
    return null;
  }

  @Override
  public Fragment newStoreGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag) {
    return null;
  }

  @Override public Fragment newStoreGridRecyclerFragment(Event event, String title) {
    return null;
  }

  @Override public Fragment newAppsTimelineFragment(String action) {
    return null;
  }

  @Override public Fragment newSubscribedStoresFragment() {
    return null;
  }

  @Override public Fragment newSearchPagerTabFragment(String query, boolean subscribedStores) {
    return null;
  }

  @Override public Fragment newSearchPagerTabFragment(String query, String storeName) {
    return null;
  }

  @Override public Fragment newDownloadsFragment() {
    return null;
  }

  @Override
  public Fragment newOtherVersionsFragment(String appName, String appImgUrl, String appPackage) {
    return null;
  }

  @Override public Fragment newRollbackFragment() {
    return null;
  }

  @Override public Fragment newExcludedUpdatesFragment() {
    return null;
  }

  @Override public Fragment newScheduledDownloadsFragment() {
    return null;
  }

  @Override
  public Fragment newScheduledDownloadsFragment(ScheduledDownloadsFragment.OpenMode openMode) {
    return null;
  }

  @Override public Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName) {
    return null;
  }

  @Override public Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName, long reviewId) {
    return null;
  }

  @Override
  public Fragment newDescriptionFragment(long appId, String storeName, String storeTheme) {
    return null;
  }

  @Override public Fragment newSocialFragment(String socialUrl, String pageTitle) {
    return null;
  }

  @Override public Fragment newSettingsFragment() {
    return null;
  }
}
