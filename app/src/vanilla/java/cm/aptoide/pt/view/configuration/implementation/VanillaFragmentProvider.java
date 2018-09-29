package cm.aptoide.pt.view.configuration.implementation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import cm.aptoide.pt.addressbook.data.Contact;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.addressbook.view.InviteFriendsFragment;
import cm.aptoide.pt.addressbook.view.PhoneInputFragment;
import cm.aptoide.pt.addressbook.view.SyncResultFragment;
import cm.aptoide.pt.addressbook.view.ThankYouConnectingFragment;
import cm.aptoide.pt.app.view.ListAppsFragment;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.app.view.OtherVersionsFragment;
import cm.aptoide.pt.comments.view.CommentListFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.presenter.InviteFriendsContract;
import cm.aptoide.pt.reviews.LatestReviewsFragment;
import cm.aptoide.pt.reviews.RateAndReviewsFragment;
import cm.aptoide.pt.search.model.SearchAdResult;
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
import cm.aptoide.pt.updates.view.excluded.ExcludedUpdatesFragment;
import cm.aptoide.pt.view.FragmentProvider;
import cm.aptoide.pt.view.feedback.SendFeedbackFragment;
import cm.aptoide.pt.view.fragment.DescriptionFragment;
import cm.aptoide.pt.view.settings.SettingsFragment;
import java.util.List;
import org.parceler.Parcels;

/**
 * Created by neuro on 10-10-2016.
 */
public class VanillaFragmentProvider implements FragmentProvider {

  @Override public Fragment newSendFeedbackFragment(String screenshotFilePath) {
    return SendFeedbackFragment.newInstance(screenshotFilePath);
  }

  @Override public Fragment newSendFeedbackFragment(String screenshotFilePath, String postId) {
    return SendFeedbackFragment.newInstance(screenshotFilePath, postId);
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
      NewAppViewFragment.OpenType openType) {
    Bundle bundle = new Bundle();
    if (!TextUtils.isEmpty(packageName)) {
      bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    }
    bundle.putSerializable(NewAppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putSerializable(NewAppViewFragment.BundleKeys.SHOULD_INSTALL.name(),
        NewAppViewFragment.OpenType.OPEN_ONLY);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newAppViewFragment(long appId, String packageName, String storeTheme,
      String storeName, String tag, String editorsBrickPosition) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putString(NewAppViewFragment.BundleKeys.EDITORS_CHOICE_POSITION.name(),
        editorsBrickPosition);
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newAppViewFragment(SearchAdResult searchAdResult, String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(NewAppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public Fragment newAppViewFragment(String packageName, NewAppViewFragment.OpenType openType) {
    Bundle bundle = new Bundle();
    if (!TextUtils.isEmpty(packageName)) {
      bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    }
    bundle.putSerializable(NewAppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), null);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override public Fragment newFragmentTopStores() {
    return FragmentTopStores.newInstance();
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
    return GetStoreWidgetsFragment.newInstance();
  }

  @Override public Fragment newGetAdsFragment() {
    return new GetAdsFragment();
  }

  @Override public Fragment newListStoresFragment() {
    return new ListStoresFragment();
  }

  @Override public Fragment newSubscribedStoresFragment(Event event, String storeTheme, String tag,
      StoreContext storeName) {
    return MyStoresFragment.newInstance(event, storeTheme, tag, storeName);
  }

  @Override
  public Fragment newOtherVersionsFragment(String appName, String appImgUrl, String appPackage) {
    return OtherVersionsFragment.newInstance(appName, appImgUrl, appPackage);
  }

  @Override public Fragment newExcludedUpdatesFragment() {
    return ExcludedUpdatesFragment.newInstance();
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

  @Override public Fragment newCommentGridRecyclerFragmentUrl(CommentType commentType, String url,
      String storeAnalyticsAction, StoreContext storeContext) {
    return CommentListFragment.newInstanceUrl(commentType, url, storeAnalyticsAction, storeContext);
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
