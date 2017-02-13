package cm.aptoide.pt.v8engine.addressbook.syncsuccess;

import cm.aptoide.pt.v8engine.addressbook.data.Contact;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class SyncSuccessPresenter implements SyncSuccessContract.UserActionsListener {

  private final SyncSuccessContract.View mSyncSuccessView;

  public SyncSuccessPresenter(SyncSuccessContract.View syncSuccessView) {
    this.mSyncSuccessView = syncSuccessView;
  }

  @Override public void loadFriends() {

  }

  @Override public void openFriend(Contact clickedContact) {

  }

  @Override public void allowFindClicked() {

  }

  @Override public void doneClicked() {
    mSyncSuccessView.finishView();
  }
}
