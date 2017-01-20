package cm.aptoide.pt.v8engine.configuration;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.ScheduledDownloadsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.TimeLineFollowFragment;
import java.util.ArrayList;

/**
 * Interface from which all fragments should be requested.
 */
public interface FragmentProvider {

  Fragment newScreenshotsViewerFragment(ArrayList<String> uris, int currentItem);

  Fragment newSendFeedbackFragment(String screenshotFilePath);

  Fragment newStoreFragment(String storeName, String storeTheme);

  Fragment newStoreFragment(String storeName);

  Fragment newStoreFragment(String storeName, StoreContext storeContext, String storeTheme);

  Fragment newStoreFragment(String storeName, StoreContext storeContext);

  Fragment newHomeFragment(String storeName, StoreContext storeContext, String storeTheme);

  Fragment newSearchFragment(String query);

  Fragment newSearchFragment(String query, boolean onlyTrustedApps);

  Fragment newSearchFragment(String query, String storeName);

  Fragment newAppViewFragment(String packageName, String storeName,
      AppViewFragment.OpenType openType);

  Fragment newAppViewFragment(String md5);

  Fragment newAppViewFragment(long appId, String packageName, AppViewFragment.OpenType openType);

  Fragment newAppViewFragment(long appId, String packageName);

  Fragment newAppViewFragment(long appId, String packageName, String storeTheme, String storeName);

  Fragment newAppViewFragment(MinimalAd minimalAd);

  Fragment newAppViewFragment(String packageName, AppViewFragment.OpenType openType);

  Fragment newFragmentTopStores();

  Fragment newUpdatesFragment();

  Fragment newLatestReviewsFragment(long storeId);

  Fragment newStoreTabGridRecyclerFragment(Event event, String storeTheme, String tag);

  Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag);

  Fragment newListAppsFragment();

  Fragment newGetStoreFragment();

  Fragment newMyStoresSubscribedFragment();

  Fragment newMyStoresFragment();

  Fragment newGetStoreWidgetsFragment();

  Fragment newListReviewsFragment();

  Fragment newGetAdsFragment();

  Fragment newListStoresFragment();

  Fragment newAppsTimelineFragment(String action);

  Fragment newSubscribedStoresFragment(Event event, String title, String storeTheme, String tag);

  Fragment newSearchPagerTabFragment(String query, boolean subscribedStores,
      boolean hasMultipleFragments);

  Fragment newSearchPagerTabFragment(String query, String storeName);

  Fragment newDownloadsFragment();

  Fragment newOtherVersionsFragment(String appName, String appImgUrl, String appPackage);

  Fragment newRollbackFragment();

  Fragment newExcludedUpdatesFragment();

  Fragment newScheduledDownloadsFragment();

  Fragment newScheduledDownloadsFragment(ScheduledDownloadsFragment.OpenMode openMode);

  Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName, String storeTheme);

  Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName, long reviewId);

  Fragment newDescriptionFragment(long appId, String packageName, String storeName,
      String storeTheme);

  Fragment newSocialFragment(String socialUrl, String pageTitle);

  Fragment newSettingsFragment();

  Fragment newCreateUserFragment();

  Fragment newTimeLineFollowStatsFragment(TimeLineFollowFragment.FollowFragmentOpenMode openMode,
      long followNumber);

  Fragment newCommentGridRecyclerFragment(CommentType commentType, String elementId);

  Fragment newCommentGridRecyclerFragmentUrl(CommentType commentType, String url);
}
