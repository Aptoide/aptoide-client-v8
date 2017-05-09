package cm.aptoide.pt.v8engine.presenter;

import android.content.Context;

/**
 * Created by jdandrade on 23/02/2017.
 */

public interface InviteFriendsContract {
  interface View {

    enum OpenMode {
      ERROR, CONTACTS_PERMISSION_DENIAL, NO_FRIENDS
    }
  }

  interface UserActionsListener {

    void allowFindClicked();

    void doneClicked();

    void shareClicked(Context context);
  }
}
