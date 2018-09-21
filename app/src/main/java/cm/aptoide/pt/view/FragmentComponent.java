package cm.aptoide.pt.view;

import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.account.view.user.ProfileStepOneFragment;
import cm.aptoide.pt.account.view.user.ProfileStepTwoFragment;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.addressbook.view.InviteFriendsFragment;
import cm.aptoide.pt.addressbook.view.PhoneInputFragment;
import cm.aptoide.pt.addressbook.view.SyncResultFragment;
import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.EditorialFragment;
import cm.aptoide.pt.app.view.MoreBundleFragment;
import cm.aptoide.pt.app.view.NewAppViewFragment;
import cm.aptoide.pt.billing.view.login.PaymentLoginFragment;
import cm.aptoide.pt.comment.CommentsFragment;
import cm.aptoide.pt.comments.view.CommentListFragment;
import cm.aptoide.pt.home.GetRewardAppCoinsAppsFragment;
import cm.aptoide.pt.home.HomeFragment;
import cm.aptoide.pt.home.apps.AppsFragment;
import cm.aptoide.pt.notification.view.InboxFragment;
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
import cm.aptoide.pt.view.app.ListStoreAppsFragment;
import cm.aptoide.pt.view.settings.MyAccountFragment;
import cm.aptoide.pt.view.wizard.WizardFragment;
import dagger.Subcomponent;

@FragmentScope @Subcomponent(modules = { FragmentModule.class })
public interface FragmentComponent {

  void inject(AddressBookFragment addressBookFragment);

  void inject(LoginSignUpCredentialsFragment loginSignUpCredentialsFragment);

  void inject(ManageUserFragment manageUserFragment);

  void inject(ManageStoreFragment manageStoreFragment);

  void inject(AppViewFragment appViewFragment);

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

  void inject(NewAppViewFragment newAppViewFragment);

  void inject(RateAndReviewsFragment rateAndReviewsFragment);

  void inject(MoreBundleFragment moreBundleFragment);

  void inject(WizardFragment wizardFragment);

  void inject(PaymentLoginFragment paymentLoginFragment);

  void inject(AppCoinsInfoFragment appCoinsInfoFragment);

  void inject(EditorialFragment editorialFragment);

  void inject(CommentsFragment commentsFragment);
}
