package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;

/**
 * Created by jdandrade on 10/02/2017.
 */
public interface AddressBookContract {
  interface View {

    void finishView();

    void changeAddressBookState(boolean checked);

    void changeTwitterState(boolean checked);

    void changeFacebookState(boolean checked);

    void setGenericPleaseWaitDialog(boolean showProgress);
  }

  interface UserActionsListener {

    void syncAddressBook();

    void syncTwitter(TwitterModel twitterModel);

    void syncFacebook(FacebookModel facebookModel);

    void getButtonsState();

    void finishViewClick();

    void aboutClick();

    void allowFindClick();

    void contactsPermissionDenied();
  }
}
