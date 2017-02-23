package cm.aptoide.pt.v8engine.addressbook.invitefriends;

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
}
