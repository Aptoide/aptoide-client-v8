package cm.aptoide.pt.v8engine.view.addressbook;

import android.support.annotation.NonNull;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.presenter.AddressBookNavigation;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.presenter.InviteFriendsContract;
import java.util.List;

/**
 * This class serves as an API to the navigation manager for the Address Book navigation. All the
 * navigation inside the AddressBook feature should use this.
 *
 * Created by jdandrade on 02/03/2017.
 */

public class AddressBookNavigationManager implements AddressBookNavigation {

  private final FragmentNavigator navigator;
  private final String exitNavigationFragmentTag;
  private final String aboutFragmentActionBarTitle;
  private final String aboutFragmentBodyMessage;

  public AddressBookNavigationManager(FragmentNavigator navigator, String exitNavigatonFragmentTag,
      String aboutFragmentActionBarTitle, String aboutFragmentBodyMessage) {
    this.navigator = navigator;
    this.exitNavigationFragmentTag = exitNavigatonFragmentTag;
    this.aboutFragmentActionBarTitle = aboutFragmentActionBarTitle;
    this.aboutFragmentBodyMessage = aboutFragmentBodyMessage;
  }

  @Override public void leaveAddressBook() {
    this.navigator.cleanBackStackUntil(exitNavigationFragmentTag);
  }

  @Override public void navigateToPhoneInputView() {
    this.navigator.navigateTo(
        V8Engine.getFragmentProvider().newPhoneInputFragment(exitNavigationFragmentTag));
  }

  @Override
  public void navigateToInviteFriendsView(@NonNull InviteFriendsContract.View.OpenMode openMode) {
    switch (openMode) {
      case ERROR:
        this.navigator.navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(InviteFriendsContract.View.OpenMode.ERROR,
                exitNavigationFragmentTag));
        break;
      case NO_FRIENDS:
        this.navigator.navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(InviteFriendsContract.View.OpenMode.NO_FRIENDS,
                exitNavigationFragmentTag));
        break;
      case CONTACTS_PERMISSION_DENIAL:
        this.navigator.navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(
                InviteFriendsContract.View.OpenMode.CONTACTS_PERMISSION_DENIAL,
                exitNavigationFragmentTag));
        break;
      default:
        Logger.d(this.getClass().getSimpleName(), "Wrong openMode type.");
    }
  }

  @Override public void showAboutFragment() {
    navigator.navigateTo(V8Engine.getFragmentProvider()
        .newDescriptionFragment(aboutFragmentActionBarTitle, aboutFragmentBodyMessage, "default"));
  }

  @Override public void showSuccessFragment(List<Contact> contacts) {
    navigator.navigateTo(
        V8Engine.getFragmentProvider().newSyncSuccessFragment(contacts, exitNavigationFragmentTag));
  }

  @Override public void navigateToThankYouConnectingFragment() {
    navigator.navigateTo(
        V8Engine.getFragmentProvider().newThankYouConnectingFragment(exitNavigationFragmentTag));
  }
}
