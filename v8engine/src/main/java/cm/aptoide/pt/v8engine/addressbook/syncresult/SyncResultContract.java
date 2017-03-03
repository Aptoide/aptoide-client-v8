package cm.aptoide.pt.v8engine.addressbook.syncresult;

import cm.aptoide.pt.v8engine.addressbook.data.Contact;

/**
 * Created by jdandrade on 13/02/2017.
 */

public interface SyncResultContract {
  interface View {

    void finishView();

    void showStore();

    void showPhoneInputFragment();

    void setProgressIndicator(boolean active);
  }

  interface UserActionsListener {

    void allowFindClicked();

    void doneClicked();
  }
}
