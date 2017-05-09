package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.v8engine.addressbook.AddressBookAnalytics;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class SyncResultPresenter implements SyncResultContract.UserActionsListener {

  private final SyncResultContract.View mSyncSuccessView;
  private final AddressBookAnalytics analytics;
  private final AddressBookNavigation addressBookNavigation;

  public SyncResultPresenter(SyncResultContract.View syncSuccessView,
      AddressBookAnalytics analytics, AddressBookNavigation addressBookNavigation) {
    this.mSyncSuccessView = syncSuccessView;
    this.addressBookNavigation = addressBookNavigation;
    this.analytics = analytics;
  }

  @Override public void allowFindClicked() {
    analytics.sendNewConnectionsAllowFriendsToFindYouEvent(
        AddressBookAnalytics.HAS_NEW_CONNECTIONS_SCREEN);
    this.addressBookNavigation.navigateToPhoneInputView();
  }

  @Override public void doneClicked() {
    analytics.sendNewConnectionsDoneEvent(AddressBookAnalytics.HAS_NEW_CONNECTIONS_SCREEN);
    this.addressBookNavigation.leaveAddressBook();
  }
}
