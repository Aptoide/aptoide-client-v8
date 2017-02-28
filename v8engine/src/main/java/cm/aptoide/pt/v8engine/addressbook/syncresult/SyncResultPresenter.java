package cm.aptoide.pt.v8engine.addressbook.syncresult;

import cm.aptoide.pt.v8engine.addressbook.data.Contact;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class SyncResultPresenter implements SyncResultContract.UserActionsListener {

  private final SyncResultContract.View mSyncSuccessView;

  public SyncResultPresenter(SyncResultContract.View syncSuccessView) {
    this.mSyncSuccessView = syncSuccessView;
  }

  @Override public void loadFriends() {
    //todo manipulate loading widget this.mSyncSuccessView.setProgressIndicator(true);

    //todo loadfriends callback and hide loading widget this.mSyncSuccessView.setProgressIndicator(false);
  }

  @Override public void openFriend(Contact clickedContact) {

  }

  @Override public void allowFindClicked() {
    this.mSyncSuccessView.showPhoneInputFragment();
  }

  @Override public void doneClicked() {
    this.mSyncSuccessView.finishView();
  }
}
