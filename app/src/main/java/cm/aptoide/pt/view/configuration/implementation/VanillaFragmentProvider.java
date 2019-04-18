package cm.aptoide.pt.view.configuration.implementation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import cm.aptoide.pt.addressbook.data.Contact;
import cm.aptoide.pt.addressbook.view.InviteFriendsFragment;
import cm.aptoide.pt.addressbook.view.PhoneInputFragment;
import cm.aptoide.pt.addressbook.view.SyncResultFragment;
import cm.aptoide.pt.addressbook.view.ThankYouConnectingFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.presenter.InviteFriendsContract;
import cm.aptoide.pt.reviews.LatestReviewsFragment;
import cm.aptoide.pt.reviews.RateAndReviewsFragment;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.fragment.DescriptionFragment;
import cm.aptoide.pt.view.settings.SettingsFragment;
import java.util.List;
import org.parceler.Parcels;

/**
 * Created by neuro on 10-10-2016.
 */
public class VanillaFragmentProvider implements FragmentProvider {

  @Override public Fragment newStoreFragment(String storeName, String storeTheme) {
    return StoreFragment.newInstance(storeName, storeTheme);
  }

  @Override public Fragment newStoreFragment(String storeName, String storeTheme,
      StoreFragment.OpenType openType) {
    return StoreFragment.newInstance(storeName, storeTheme, openType);
  }

  @Override public Fragment newStoreFragment(long userId, String storeTheme,
      StoreFragment.OpenType openType) {
    return StoreFragment.newInstance(userId, storeTheme, openType);
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putSerializable(AppViewFragment.BundleKeys.SHOULD_INSTALL.name(),
        AppViewFragment.OpenType.OPEN_ONLY);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag, String editorsBrickPosition) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putString(AppViewFragment.BundleKeys.EDITORS_CHOICE_POSITION.name(),
        editorsBrickPosition);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newAppViewFragment(SearchAdResult searchAdResult, String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(AppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public Fragment newAppViewFragment(String packageName, AppViewFragment.OpenType openType) {
    Bundle bundle = new Bundle();
    if (!TextUtils.isEmpty(packageName)) {
      bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    }
    bundle.putSerializable(AppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), null);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newLatestReviewsFragment(long storeId, StoreContext storeContext) {
    return LatestReviewsFragment.newInstance(storeId, storeContext);
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String storeTheme, String tag,
      StoreContext storeContext, boolean addAdultFilter) {
    return StoreTabGridRecyclerFragment.newInstance(event, storeTheme, tag, storeContext);
  }

  @Override
  public Fragment newStoreTabGridRecyclerFragment(Event event, String title, String storeTheme,
      String tag, StoreContext storeContext, boolean addAdultFilter) {
    return StoreTabGridRecyclerFragment.newInstance(event, title, storeTheme, tag, storeContext);
  }

  @Override public Fragment newSubscribedStoresFragment(Event event, String storeTheme, String tag,
      StoreContext storeName) {
    return MyStoresFragment.newInstance(event, storeTheme, tag, storeName);
  }

  @Override public Fragment newRateAndReviewsFragment(long appId, String appName, String storeName,
      String packageName, long reviewId) {
    return RateAndReviewsFragment.newInstance(appId, appName, storeName, packageName, reviewId);
  }

  @Override
  public Fragment newDescriptionFragment(String appName, String description, String storeTheme) {
    return DescriptionFragment.newInstance(appName, description, storeTheme);
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

  @Override public Fragment newThankYouConnectingFragment(String tag) {
    return ThankYouConnectingFragment.newInstance(tag);
  }

  @Override public Fragment newTimeLineFollowersFragment(String storeTheme, String title,
      StoreContext storeContext) {
    return TimeLineFollowersFragment.newInstanceUsingUser(storeTheme, title, storeContext);
  }
}
