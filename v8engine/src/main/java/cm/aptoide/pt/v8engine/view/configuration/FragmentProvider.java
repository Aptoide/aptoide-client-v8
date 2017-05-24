package cm.aptoide.pt.v8engine.view.configuration;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.presenter.InviteFriendsContract;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.downloads.scheduled.ScheduledDownloadsFragment;
import cm.aptoide.pt.v8engine.view.store.StoreFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface from which all fragments should be requested.
 */
public interface FragmentProvider {

  Fragment newScreenshotsViewerFragment(ArrayList<String> uris, int currentItem);

  Fragment newSendFeedbackFragment(String screenshotFilePath);

  Fragment newStoreFragment(String storeName, String storeTheme);

  Fragment newStoreFragment(String storeName, String storeTheme, StoreFragment.OpenType openType);

  Fragment newStoreFragment(String storeName, String storeTheme, Event.Name defaultTab,
      StoreFragment.OpenType openType);

  Fragment newStoreFragment(long userId, String storeTheme, Event.Name defaultTab,
      StoreFragment.OpenType openType);

  Fragment newStoreFragment(long userId, String storeTheme, StoreFragment.OpenType openType);

  /**
   * @param storeContext is needed to give context to fragment ex: store downloads vs global
   * downloads
   */
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

  /**
   * @param storeContext is needed to give context to fragment ex: store downloads vs global
   * downloads
   */
  Fragment newStoreTabGridRecyclerFragment(Event event, String storeTheme, String tag,
      StoreContext storeContext);

  /**
   * @param storeContext is needed to give context to fragment ex: store downloads vs global
   * downloads
   */
  Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme, String tag,
      StoreContext storeContext);

  Fragment newListAppsFragment();

  Fragment newGetStoreFragment();

  Fragment newMyStoresSubscribedFragment();

  Fragment newMyStoresFragment();

  Fragment newGetStoreWidgetsFragment();

  Fragment newListReviewsFragment();

  Fragment newGetAdsFragment();

  Fragment newListStoresFragment();

  Fragment newAppsTimelineFragment(String action, Long userId, Long storeId,
      StoreContext storeContext);

  Fragment newSubscribedStoresFragment(Event event, String storeTheme, String tag);

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

  Fragment newDescriptionFragment(String appName, String description, String storeTheme);

  Fragment newSocialFragment(String socialUrl, String pageTitle);

  Fragment newSettingsFragment();

  Fragment newTimeLineFollowersUsingUserIdFragment(Long id, long followerNumber, String storeTheme);

  Fragment newTimeLineFollowingFragmentUsingUserId(Long id, long followingNumber,
      String storeTheme);

  Fragment newTimeLineFollowersUsingStoreIdFragment(Long id, long followerNumber,
      String storeTheme);

  Fragment newTimeLineFollowingFragmentUsingStoreId(Long id, long followerNumber,
      String storeTheme);

  Fragment newTimeLineLikesFragment(String cardUid, long numberOfLikes, String storeTheme);

  Fragment newCommentGridRecyclerFragment(CommentType commentType, String elementId);

  Fragment newCommentGridRecyclerFragmentUrl(CommentType commentType, String url);

  Fragment newCommentGridRecyclerFragmentWithCommentDialogOpen(CommentType commentType,
      String elementId);

  Fragment newAddressBookFragment();

  Fragment newSyncSuccessFragment(List<Contact> contacts, String tag);

  Fragment newPhoneInputFragment(String tag);

  Fragment newInviteFriendsFragment(InviteFriendsContract.View.OpenMode openMode, String tag);

  Fragment newSpotShareFragment(boolean showToolbar);

  Fragment newThankYouConnectingFragment(String tag);

  Fragment newTimeLineFollowersFragment(long followerNumber, String storeTheme);

  Fragment newRecommendedStoresFragment();
}
