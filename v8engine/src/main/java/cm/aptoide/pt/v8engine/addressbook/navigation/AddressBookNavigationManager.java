package cm.aptoide.pt.v8engine.addressbook.navigation;

import android.support.annotation.NonNull;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigation.NavigationManagerV4;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.addressbook.data.Contact;
import cm.aptoide.pt.v8engine.addressbook.invitefriends.InviteFriendsContract;
import java.util.List;

/**
 * This class serves as an API to the navigation manager for the Address Book navigation. All the
 * navigation
 *
 * Created by jdandrade on 02/03/2017.
 */

public class AddressBookNavigationManager implements AddressBookNavigation {

  private final NavigationManagerV4 navigationManager;
  private final String exitNavigationFragmentTag;
  private final String aboutFragmentActionBarTitle;
  private final String aboutFragmentBodyMessage;

  public AddressBookNavigationManager(NavigationManagerV4 navigationManager,
      String exitNavigatonFragmentTag, String aboutFragmentActionBarTitle,
      String aboutFragmentBodyMessage) {
    this.navigationManager = navigationManager;
    this.exitNavigationFragmentTag = exitNavigatonFragmentTag;
    this.aboutFragmentActionBarTitle = aboutFragmentActionBarTitle;
    this.aboutFragmentBodyMessage = aboutFragmentBodyMessage;
  }

  @Override public void leaveAddressBook() {
    this.navigationManager.cleanBackStackUntil(exitNavigationFragmentTag);
  }

  @Override public void navigateToPhoneInputView() {
    this.navigationManager.navigateTo(
        V8Engine.getFragmentProvider().newPhoneInputFragment(exitNavigationFragmentTag));
  }

  @Override
  public void navigateToInviteFriendsView(@NonNull InviteFriendsContract.View.OpenMode openMode) {
    switch (openMode) {
      case ERROR:
        this.navigationManager.navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(InviteFriendsContract.View.OpenMode.ERROR,
                exitNavigationFragmentTag));
        break;
      case NO_FRIENDS:
        this.navigationManager.navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(InviteFriendsContract.View.OpenMode.NO_FRIENDS,
                exitNavigationFragmentTag));
        break;
      case CONTACTS_PERMISSION_DENIAL:
        this.navigationManager.navigateTo(V8Engine.getFragmentProvider()
            .newInviteFriendsFragment(
                InviteFriendsContract.View.OpenMode.CONTACTS_PERMISSION_DENIAL,
                exitNavigationFragmentTag));
      default:
        Logger.d(this.getClass().getSimpleName(), "Wrong openMode type.");
    }
  }

  @Override public void showAboutFragment() {
    navigationManager.navigateTo(V8Engine.getFragmentProvider()
        .newDescriptionFragment(aboutFragmentActionBarTitle, aboutFragmentBodyMessage, "default"));
  }

  @Override public void showSuccessFragment(List<Contact> contacts) {
    navigationManager.navigateTo(
        V8Engine.getFragmentProvider().newSyncSuccessFragment(contacts, exitNavigationFragmentTag));
  }

  @Override public void navigateToThankYouConnectingFragment() {
    navigationManager.navigateTo(
        V8Engine.getFragmentProvider().newThankYouConnectingFragment(exitNavigationFragmentTag));
  }
}
