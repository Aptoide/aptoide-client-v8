package cm.aptoide.pt.v8engine.addressbook;

import cm.aptoide.pt.preferences.managed.ManagerPreferences;

/**
 * Created by jdandrade on 10/02/2017.
 */
public class AddressBookPresenter implements AddressBookContract.UserActionsListener {

  private final AddressBookContract.View mAddressBookView;

  public AddressBookPresenter(AddressBookContract.View addressBookView) {
    this.mAddressBookView = addressBookView;
  }

  @Override public void syncAddressBook() {
    ManagerPreferences.setAddressBookAsSynced();
    mAddressBookView.changeAddressBookState(true);
    mAddressBookView.showSuccessFragment();
  }

  @Override public void syncTwitter() {
    ManagerPreferences.setTwitterAsSynced();
    mAddressBookView.changeTwitterState(true);
  }

  @Override public void syncFacebook() {
    ManagerPreferences.setFacebookAsSynced();
    mAddressBookView.changeFacebookState(true);
  }

  @Override public void getButtonsState() {
    mAddressBookView.changeAddressBookState(ManagerPreferences.getAddressBookSyncState());
    mAddressBookView.changeTwitterState(ManagerPreferences.getTwitterSyncState());
    mAddressBookView.changeFacebookState(ManagerPreferences.getFacebookSyncState());
  }

  @Override public void finishViewClick() {
    mAddressBookView.finishView();
  }

  @Override public void aboutClick() {
    mAddressBookView.showAboutFragment();
  }
}
