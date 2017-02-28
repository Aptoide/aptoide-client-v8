package cm.aptoide.pt.v8engine.addressbook.invitefriends;

import android.content.Context;

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

    void shareClicked(Context context);
  }
}
