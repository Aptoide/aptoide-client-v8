package cm.aptoide.pt.v8engine.addressbook.invitefriends;

/**
 * Created by jdandrade on 23/02/2017.
 */

public interface InviteFriendsContract {
  interface View {

    void showPhoneInputFragment();

    void finishView();
  }

  interface UserActionsListener {

    void allowFindClicked();

    void doneClicked();
  }
}
