package cm.aptoide.pt.view;

import cm.aptoide.pt.FlavourFragmentModule;
import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.account.view.user.ProfileStepOneFragment;
import cm.aptoide.pt.account.view.user.ProfileStepTwoFragment;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.addressbook.view.InviteFriendsFragment;
import cm.aptoide.pt.addressbook.view.PhoneInputFragment;
import cm.aptoide.pt.addressbook.view.SyncResultFragment;
import cm.aptoide.pt.addressbook.view.ThankYouConnectingFragment;
import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.MoreBundleFragment;
import cm.aptoide.pt.billing.view.login.PaymentLoginFragment;
import cm.aptoide.pt.comments.view.CommentListFragment;
import cm.aptoide.pt.editorial.EditorialFragment;
import cm.aptoide.pt.editorialList.EditorialListFragment;
import cm.aptoide.pt.home.GetRewardAppCoinsAppsFragment;
import cm.aptoide.pt.home.HomeContainerFragment;
import cm.aptoide.pt.home.HomeFragment;
import cm.aptoide.pt.home.apps.AppsFragment;
import cm.aptoide.pt.home.apps.SeeMoreAppcFragment;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.promotions.ClaimPromotionDialogFragment;
import cm.aptoide.pt.promotions.PromotionsFragment;
import cm.aptoide.pt.reviews.LatestReviewsFragment;
import cm.aptoide.pt.reviews.RateAndReviewsFragment;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.share.NotLoggedInShareFragment;
import cm.aptoide.pt.store.view.FragmentTopStores;
import cm.aptoide.pt.store.view.ListStoresFragment;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StoreTabWidgetsGridRecyclerFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.store.view.my.MyStoresSubscribedFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowersFragment;
import cm.aptoide.pt.timeline.view.follow.TimeLineFollowingFragment;
import cm.aptoide.pt.view.app.ListStoreAppsFragment;
import cm.aptoide.pt.view.fragment.DescriptionFragment;
import cm.aptoide.pt.view.fragment.GridRecyclerSwipeWithToolbarFragment;
import cm.aptoide.pt.view.settings.MyAccountFragment;
import cm.aptoide.pt.view.settings.SettingsFragment;
import cm.aptoide.pt.view.splashscreen.SplashScreenFragment;
import cm.aptoide.pt.view.wizard.WizardFragment;
import cm.aptoide.pt.view.wizard.WizardFragmentProvider;
import cm.aptoide.pt.view.wizard.WizardPageTwoFragment;
import dagger.Subcomponent;

@FragmentScope @Subcomponent(modules = { FragmentModule.class, FlavourFragmentModule.class })
public interface FragmentComponent {

  void inject(AddressBookFragment addressBookFragment);

  void inject(LoginSignUpCredentialsFragment loginSignUpCredentialsFragment);

  void inject(ManageUserFragment manageUserFragment);

  void inject(ManageStoreFragment manageStoreFragment);

  void inject(SearchResultFragment searchResultFragment);

  void inject(StoreFragment storeFragment);

  void inject(SyncResultFragment syncResultFragment);

  void inject(InviteFriendsFragment inviteFriendsFragment);

  void inject(PhoneInputFragment phoneInputFragment);

  void inject(CommentListFragment commentListFragment);

  void inject(FragmentTopStores fragmentTopStores);

  void inject(LatestReviewsFragment latestReviewsFragment);

  void inject(ListStoresFragment listStoresFragment);

  void inject(MyStoresSubscribedFragment myStoresSubscribedFragment);

  void inject(StoreTabWidgetsGridRecyclerFragment storeTabWidgetsGridRecyclerFragment);

  void inject(MyStoresFragment myStoresFragment);

  void inject(InboxFragment inboxFragment);

  void inject(NotLoggedInShareFragment notLoggedInShareFragment);

  void inject(ProfileStepOneFragment profileStepOneFragment);

  void inject(ProfileStepTwoFragment profileStepTwoFragment);

  void inject(ListStoreAppsFragment listStoreAppsFragment);

  void inject(HomeFragment homeFragment);

  void inject(AppsFragment appsFragment);

  void inject(MyAccountFragment myAccountFragment);

  void inject(GetRewardAppCoinsAppsFragment getRewardAppCoinsAppsFragment);

  void inject(AppViewFragment appViewFragment);

  void inject(RateAndReviewsFragment rateAndReviewsFragment);

  void inject(MoreBundleFragment moreBundleFragment);

  void inject(WizardFragment wizardFragment);

  void inject(PaymentLoginFragment paymentLoginFragment);

  void inject(AppCoinsInfoFragment appCoinsInfoFragment);

  void inject(EditorialFragment editorialFragment);

  void inject(PromotionsFragment promotionsFragment);

  void inject(DescriptionFragment descriptionFragment);

  void inject(TimeLineFollowingFragment timeLineFollowingFragment);

  void inject(TimeLineFollowersFragment timeLineFollowersFragment);

  void inject(GridRecyclerSwipeWithToolbarFragment gridRecyclerSwipeWithToolbarFragment);

  void inject(SettingsFragment settingsFragment);

  void inject(ThankYouConnectingFragment thankYouConnectingFragment);

  void inject(WizardFragmentProvider wizardFragmentProvider);

  void inject(WizardPageTwoFragment wizardPageTwoFragment);

  void inject(ClaimPromotionDialogFragment claimPromotionDialogFragment);

  void inject(HomeContainerFragment homeContainerFragment);

  void inject(SplashScreenFragment splashScreenFragment);

  void inject(EditorialListFragment editorialListFragment);

  void inject(SeeMoreAppcFragment seeMoreAppcFragment);
}
