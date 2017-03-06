package cm.aptoide.pt.v8engine.addressbook.syncresult;

import cm.aptoide.pt.v8engine.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class SyncResultPresenter implements SyncResultContract.UserActionsListener {

  private final SyncResultContract.View mSyncSuccessView;
  private final AddressBookAnalytics analytics;

  public SyncResultPresenter(SyncResultContract.View syncSuccessView,
      AddressBookAnalytics analytics) {
    this.mSyncSuccessView = syncSuccessView;
    this.analytics = analytics;
  }

  @Override public void allowFindClicked() {
    analytics.sendNewConnectionsAllowFriendsToFindYouEvent(AddressBookAnalytics.HAS_NEW_CONNECTIONS_SCREEN);
    this.mSyncSuccessView.showPhoneInputFragment();
  }

  @Override public void doneClicked() {
    analytics.sendNewConnectionsDoneEvent(AddressBookAnalytics.HAS_NEW_CONNECTIONS_SCREEN);
    this.mSyncSuccessView.finishView();
  }
}
