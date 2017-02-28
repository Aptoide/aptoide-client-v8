package cm.aptoide.pt.v8engine.addressbook.invitefriends;

import android.content.Context;
import android.content.Intent;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class InviteFriendsPresenter implements InviteFriendsContract.UserActionsListener {

  private final InviteFriendsContract.View mInviteFriendsView;

  public InviteFriendsPresenter(InviteFriendsContract.View inviteFriendsView) {
    this.mInviteFriendsView = inviteFriendsView;
  }

  @Override public void allowFindClicked() {
    this.mInviteFriendsView.showPhoneInputFragment();
  }

  @Override public void doneClicked() {
    this.mInviteFriendsView.finishView();
  }

  @Override public void shareClicked(Context context) {
    
  }
}
