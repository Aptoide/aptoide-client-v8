package cm.aptoide.pt.v8engine.addressbook;

/**
 * Created by jdandrade on 10/02/2017.
 */
public interface AddressBookContract {
  interface View {
    void finishView();

    void changeAddressBookState(boolean checked);

    void changeTwitterState(boolean checked);

    void changeFacebookState(boolean checked);
  }

  interface UserActionsListener {
    void syncAddressBook();

    void syncTwitter();

    void syncFacebook();

    void getButtonsState();

    void finishViewClick();
  }
}
