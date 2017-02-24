package cm.aptoide.pt.v8engine.addressbook;

import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 10/02/2017.
 */
public class AddressBookPresenter implements AddressBookContract.UserActionsListener {

  private final AddressBookContract.View mAddressBookView;
  private final ContactsRepository mContactsRepository;

  public AddressBookPresenter(AddressBookContract.View addressBookView,
      ContactsRepository contactsRepository) {
    this.mAddressBookView = addressBookView;
    this.mContactsRepository = contactsRepository;
  }

  @Override public void syncAddressBook() {
    mAddressBookView.setAddressBookProgressIndicator(true);
    mContactsRepository.getContacts(contacts -> {
      if (!contacts.isEmpty()) {
        ManagerPreferences.setAddressBookAsSynced();
        mAddressBookView.changeAddressBookState(true);
        mAddressBookView.showSuccessFragment(contacts);
        mAddressBookView.setAddressBookProgressIndicator(false);
      }
      mAddressBookView.showInviteFriendsFragment();
    });
  }

  @Override public void syncTwitter(TwitterModel twitterModel) {
    mContactsRepository.getTwitterContacts(twitterModel, contacts -> Observable.just(contacts)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(ignore -> {
          mAddressBookView.changeTwitterState(true);
          if (!contacts.isEmpty()) {
            ManagerPreferences.setTwitterAsSynced();
            mAddressBookView.showSuccessFragment(contacts);
          } else {
            mAddressBookView.showInviteFriendsFragment();
          }
        }));
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
