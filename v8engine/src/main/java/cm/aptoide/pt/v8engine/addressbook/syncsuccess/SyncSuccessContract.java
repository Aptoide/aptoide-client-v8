package cm.aptoide.pt.v8engine.addressbook.syncsuccess;

import cm.aptoide.pt.v8engine.addressbook.data.Contact;

/**
 * Created by jdandrade on 13/02/2017.
 */

public interface SyncSuccessContract {
  interface View {

    void finishView();

    void showStore();

    void showPhoneInputFragment();
  }

  interface UserActionsListener {

    void loadFriends();

    void openFriend(Contact clickedContact);

    void allowFindClicked();

    void doneClicked();
  }
}
