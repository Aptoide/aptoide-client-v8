package cm.aptoide.pt.view;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.addressbook.data.Contact;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.view.StoreFragment;
import java.util.List;

/**
 * Interface from which all fragments should be requested.
 *
 * @deprecated use specific navigator for each presenter/lifecycle manager instead. Inside those
 * navigators instantiate the proper fragment or activity.
 */
public interface FragmentProvider {

  @Deprecated Fragment newStoreFragment(String storeName, String storeTheme);

  @Deprecated Fragment newStoreFragment(String storeName, String storeTheme,
      StoreFragment.OpenType openType);

  @Deprecated Fragment newStoreFragment(long userId, String storeTheme,
      StoreFragment.OpenType openType);

  @Deprecated Fragment newAppViewFragment(long appId, String packageName, String tag);

  @Deprecated Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag);

  @Deprecated Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag, String editorsBrickPosition);

  @Deprecated Fragment newAppViewFragment(SearchAdResult searchAdResult, String tag);

  @Deprecated Fragment newAppViewFragment(String packageName, AppViewFragment.OpenType openType);

  @Deprecated Fragment newLatestReviewsFragment(long storeId, StoreContext storeContext);

  /**
   * @param storeContext is needed to give context to fragment ex: store downloads vs global
   * downloads
   * @param addAdultFilter When true, adds adult switch to Fragment's bottom.
   */
  @Deprecated Fragment newStoreTabGridRecyclerFragment(Event event, String storeTheme, String tag,
      StoreContext storeContext, boolean addAdultFilter);

  /**
   * @param storeContext is needed to give context to fragment ex: store downloads vs global
   * downloads
   * @param addAdultFilter When true, adds adult switch to Fragment's bottom.
   */
  @Deprecated Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag, StoreContext storeContext, boolean addAdultFilter);

  @Deprecated Fragment newSubscribedStoresFragment(Event event, String storeTheme, String tag,
      StoreContext storeName);

  @Deprecated Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName, long reviewId);

  @Deprecated Fragment newDescriptionFragment(String appName, String description,
      String storeTheme);

  @Deprecated Fragment newSettingsFragment();

  @Deprecated Fragment newTimeLineFollowersUsingUserIdFragment(Long id, String storeTheme,
      String title, StoreContext storeName);

  @Deprecated Fragment newTimeLineFollowingFragmentUsingUserId(Long id, String storeTheme,
      String title, StoreContext storeContext);

  @Deprecated Fragment newTimeLineFollowersUsingStoreIdFragment(Long id, String storeTheme,
      String title, StoreContext storeContext);

  @Deprecated Fragment newTimeLineFollowingFragmentUsingStoreId(Long id, String storeTheme,
      String title, StoreContext storeName);

  @Deprecated Fragment newSyncSuccessFragment(List<Contact> contacts, String tag);

  @Deprecated Fragment newPhoneInputFragment(String tag);

  @Deprecated Fragment newThankYouConnectingFragment(String tag);

  @Deprecated Fragment newTimeLineFollowersFragment(String storeTheme, String title,
      StoreContext storeContext);
}
