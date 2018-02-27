package cm.aptoide.pt.view;

import cm.aptoide.pt.account.view.LoginSignUpCredentialsFragment;
import cm.aptoide.pt.account.view.MyAccountFragment;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.user.ManageUserFragment;
import cm.aptoide.pt.account.view.user.ProfileStepOneFragment;
import cm.aptoide.pt.account.view.user.ProfileStepTwoFragment;
import cm.aptoide.pt.addressbook.view.AddressBookFragment;
import cm.aptoide.pt.addressbook.view.InviteFriendsFragment;
import cm.aptoide.pt.addressbook.view.PhoneInputFragment;
import cm.aptoide.pt.addressbook.view.SyncResultFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.comments.view.CommentListFragment;
import cm.aptoide.pt.download.view.DownloadsFragment;
import cm.aptoide.pt.notification.view.InboxFragment;
import cm.aptoide.pt.reviews.LatestReviewsFragment;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.share.NotLoggedInShareFragment;
import cm.aptoide.pt.social.commentslist.PostCommentsFragment;
import cm.aptoide.pt.social.view.TimelineFragment;
import cm.aptoide.pt.store.view.FragmentTopStores;
import cm.aptoide.pt.store.view.ListStoresFragment;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StoreTabWidgetsGridRecyclerFragment;
import cm.aptoide.pt.store.view.home.HomeFragment;
import cm.aptoide.pt.store.view.my.MyStoresSubscribedFragment;
import cm.aptoide.pt.timeline.post.PostFragment;
import cm.aptoide.pt.updates.view.UpdatesFragment;
import cm.aptoide.pt.view.app.ListStoreAppsFragment;
import dagger.Subcomponent;

@FragmentScope @Subcomponent(modules = { FragmentModule.class })
public interface FragmentComponent {

  void inject(AddressBookFragment addressBookFragment);

  void inject(LoginSignUpCredentialsFragment loginSignUpCredentialsFragment);

  void inject(ManageUserFragment manageUserFragment);

  void inject(ManageStoreFragment manageStoreFragment);

  void inject(PostCommentsFragment postCommentsFragment);

  void inject(PostFragment postFragment);

  void inject(AppViewFragment appViewFragment);

  void inject(HomeFragment homeFragment);

  void inject(SearchResultFragment searchResultFragment);

  void inject(StoreFragment storeFragment);

  void inject(SyncResultFragment syncResultFragment);

  void inject(InviteFriendsFragment inviteFriendsFragment);

  void inject(PhoneInputFragment phoneInputFragment);

  void inject(TimelineFragment timelineFragment);

  void inject(CommentListFragment commentListFragment);

  void inject(FragmentTopStores fragmentTopStores);

  void inject(LatestReviewsFragment latestReviewsFragment);

  void inject(ListStoresFragment listStoresFragment);

  void inject(MyStoresSubscribedFragment myStoresSubscribedFragment);

  void inject(StoreTabWidgetsGridRecyclerFragment storeTabWidgetsGridRecyclerFragment);

  void inject(InboxFragment inboxFragment);

  void inject(MyAccountFragment myAccountFragment);

  void inject(NotLoggedInShareFragment notLoggedInShareFragment);

  void inject(ProfileStepOneFragment profileStepOneFragment);

  void inject(ProfileStepTwoFragment profileStepTwoFragment);

  void inject(DownloadsFragment downloadsFragment);

  void inject(UpdatesFragment updatesFragment);

  void inject(ListStoreAppsFragment listStoreAppsFragment);
}
