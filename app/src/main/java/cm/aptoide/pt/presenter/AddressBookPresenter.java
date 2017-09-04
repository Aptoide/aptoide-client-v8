package cm.aptoide.pt.presenter;

import android.content.SharedPreferences;
import cm.aptoide.pt.addressbook.AddressBookAnalytics;
import cm.aptoide.pt.addressbook.data.ContactsRepository;
import cm.aptoide.pt.dataprovider.model.v7.FacebookModel;
import cm.aptoide.pt.dataprovider.model.v7.TwitterModel;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
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
  private final SharedPreferences sharedPreferences;

  public AddressBookPresenter(AddressBookContract.View addressBookView,
      ContactsRepository contactsRepository, AddressBookAnalytics addressBookAnalytics,
      AddressBookNavigation addressBookNavigationManager, SharedPreferences sharedPreferences) {
    this.view = addressBookView;
    this.contactsRepository = contactsRepository;
    this.navigationManager = addressBookNavigationManager;
    this.analytics = addressBookAnalytics;
    this.sharedPreferences = sharedPreferences;
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
            ManagerPreferences.setAddressBookAsSynced(sharedPreferences);
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
                ManagerPreferences.setTwitterAsSynced(sharedPreferences);
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
                ManagerPreferences.setFacebookAsSynced(sharedPreferences);
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
    view.changeAddressBookState(ManagerPreferences.getAddressBookSyncState(sharedPreferences));
    view.changeTwitterState(ManagerPreferences.getTwitterSyncState(sharedPreferences));
    view.changeFacebookState(ManagerPreferences.getFacebookSyncState(sharedPreferences));
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
