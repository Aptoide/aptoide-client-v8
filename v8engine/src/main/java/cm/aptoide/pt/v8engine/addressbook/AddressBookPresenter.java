package cm.aptoide.pt.v8engine.addressbook;

import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepository;
import cm.aptoide.pt.v8engine.addressbook.invitefriends.InviteFriendsFragment;
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
    mAddressBookView.setGenericPleaseWaitDialog(true);
    mContactsRepository.getContacts((contacts, success) -> Observable.just(contacts)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(ignore -> {
          if (!success) {
            mAddressBookView.showInviteFriendsFragment(
                InviteFriendsFragment.InviteFriendsFragmentOpenMode.ERROR);
            mAddressBookView.setGenericPleaseWaitDialog(false);
          } else {
            mAddressBookView.changeAddressBookState(true);
            ManagerPreferences.setAddressBookAsSynced();
            if (!contacts.isEmpty()) {
              mAddressBookView.showSuccessFragment(contacts);
              mAddressBookView.setGenericPleaseWaitDialog(false);
            } else {
              mAddressBookView.showInviteFriendsFragment(
                  InviteFriendsFragment.InviteFriendsFragmentOpenMode.NO_FRIENDS);
              mAddressBookView.setGenericPleaseWaitDialog(false);
            }
          }
        }));
  }

  @Override public void syncTwitter(TwitterModel twitterModel) {
    mContactsRepository.getTwitterContacts(twitterModel,
        (contacts, success) -> Observable.just(contacts)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(ignore -> {
              if (!success) {
                mAddressBookView.showInviteFriendsFragment(
                    InviteFriendsFragment.InviteFriendsFragmentOpenMode.ERROR);
                mAddressBookView.setGenericPleaseWaitDialog(false);
              } else {
                mAddressBookView.changeTwitterState(true);
                ManagerPreferences.setTwitterAsSynced();
                if (!contacts.isEmpty()) {
                  mAddressBookView.showSuccessFragment(contacts);
                } else {
                  mAddressBookView.showInviteFriendsFragment(
                      InviteFriendsFragment.InviteFriendsFragmentOpenMode.NO_FRIENDS);
                }
              }
            }));
  }

  @Override public void syncFacebook(FacebookModel facebookModel) {
    mContactsRepository.getFacebookContacts(facebookModel,
        (contacts, success) -> Observable.just(contacts)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(ignore -> {
              if (!success) {
                mAddressBookView.showInviteFriendsFragment(
                    InviteFriendsFragment.InviteFriendsFragmentOpenMode.ERROR);
                mAddressBookView.setGenericPleaseWaitDialog(false);
              } else {
                mAddressBookView.changeFacebookState(true);
                ManagerPreferences.setFacebookAsSynced();
                if (!contacts.isEmpty()) {
                  mAddressBookView.showSuccessFragment(contacts);
                } else {
                  mAddressBookView.showInviteFriendsFragment(
                      InviteFriendsFragment.InviteFriendsFragmentOpenMode.NO_FRIENDS);
                }
              }
            }));
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
