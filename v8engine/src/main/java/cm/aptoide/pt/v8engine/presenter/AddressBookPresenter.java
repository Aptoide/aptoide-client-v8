package cm.aptoide.pt.v8engine.presenter;

import cm.aptoide.pt.model.v7.FacebookModel;
import cm.aptoide.pt.model.v7.TwitterModel;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.v8engine.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsRepository;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by jdandrade on 10/02/2017.
 */
public class AddressBookPresenter implements AddressBookContract.UserActionsListener {

  private final AddressBookContract.View view;
  private final ContactsRepository contactsRepository;
  private final AddressBookAnalytics analytics;
  private final AddressBookNavigation navigationManager;

  public AddressBookPresenter(AddressBookContract.View addressBookView,
      ContactsRepository contactsRepository, AddressBookAnalytics addressBookAnalytics,
      AddressBookNavigation addressBookNavigationManager) {
    this.view = addressBookView;
    this.contactsRepository = contactsRepository;
    this.navigationManager = addressBookNavigationManager;
    this.analytics = addressBookAnalytics;
  }

  @Override public void syncAddressBook() {
    view.setGenericPleaseWaitDialog(true);
    contactsRepository.getContacts((contacts, success) -> Observable.just(contacts)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(ignore -> {
          if (!success) {
            navigationManager.navigateToInviteFriendsView(
                InviteFriendsContract.View.OpenMode.ERROR);
            view.setGenericPleaseWaitDialog(false);
          } else {
            view.changeAddressBookState(true);
            ManagerPreferences.setAddressBookAsSynced();
            if (!contacts.isEmpty()) {
              navigationManager.showSuccessFragment(contacts);
              view.setGenericPleaseWaitDialog(false);
            } else {
              navigationManager.navigateToInviteFriendsView(
                  InviteFriendsContract.View.OpenMode.NO_FRIENDS);
              view.setGenericPleaseWaitDialog(false);
            }
          }
        }));
  }

  @Override public void syncTwitter(TwitterModel twitterModel) {
    analytics.sendSyncTwitterEvent();
    contactsRepository.getTwitterContacts(twitterModel,
        (contacts, success) -> Observable.just(contacts)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(ignore -> {
              if (!success) {
                navigationManager.navigateToInviteFriendsView(
                    InviteFriendsContract.View.OpenMode.ERROR);
                view.setGenericPleaseWaitDialog(false);
              } else {
                view.changeTwitterState(true);
                ManagerPreferences.setTwitterAsSynced();
                if (!contacts.isEmpty()) {
                  navigationManager.showSuccessFragment(contacts);
                } else {
                  navigationManager.navigateToInviteFriendsView(
                      InviteFriendsContract.View.OpenMode.NO_FRIENDS);
                }
              }
            }));
  }

  @Override public void syncFacebook(FacebookModel facebookModel) {
    analytics.sendSyncFacebookEvent();
    contactsRepository.getFacebookContacts(facebookModel,
        (contacts, success) -> Observable.just(contacts)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(ignore -> {
              if (!success) {
                navigationManager.navigateToInviteFriendsView(
                    InviteFriendsContract.View.OpenMode.ERROR);
                view.setGenericPleaseWaitDialog(false);
              } else {
                view.changeFacebookState(true);
                ManagerPreferences.setFacebookAsSynced();
                if (!contacts.isEmpty()) {
                  navigationManager.showSuccessFragment(contacts);
                } else {
                  navigationManager.navigateToInviteFriendsView(
                      InviteFriendsContract.View.OpenMode.NO_FRIENDS);
                }
              }
            }));
  }

  @Override public void getButtonsState() {
    view.changeAddressBookState(ManagerPreferences.getAddressBookSyncState());
    view.changeTwitterState(ManagerPreferences.getTwitterSyncState());
    view.changeFacebookState(ManagerPreferences.getFacebookSyncState());
  }

  @Override public void finishViewClick() {
    view.finishView();
  }

  @Override public void aboutClick() {
    analytics.sendHowAptoideUsesYourDataEvent();
    navigationManager.showAboutFragment();
  }

  @Override public void allowFindClick() {
    navigationManager.navigateToPhoneInputView();
  }

  @Override public void contactsPermissionDenied() {
    navigationManager.navigateToInviteFriendsView(
        InviteFriendsContract.View.OpenMode.CONTACTS_PERMISSION_DENIAL);
  }
}
