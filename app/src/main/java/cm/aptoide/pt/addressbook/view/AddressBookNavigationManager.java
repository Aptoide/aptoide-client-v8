package cm.aptoide.pt.addressbook.view;

import android.support.annotation.NonNull;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.addressbook.data.Contact;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.presenter.AddressBookNavigation;
import cm.aptoide.pt.presenter.InviteFriendsContract;
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
    this.navigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newPhoneInputFragment(exitNavigationFragmentTag), true);
  }

  @Override
  public void navigateToInviteFriendsView(@NonNull InviteFriendsContract.View.OpenMode openMode) {
    switch (openMode) {
      case ERROR:
        this.navigator.navigateTo(AptoideApplication.getFragmentProvider()
            .newInviteFriendsFragment(InviteFriendsContract.View.OpenMode.ERROR,
                exitNavigationFragmentTag), true);
        break;
      case NO_FRIENDS:
        this.navigator.navigateTo(AptoideApplication.getFragmentProvider()
            .newInviteFriendsFragment(InviteFriendsContract.View.OpenMode.NO_FRIENDS,
                exitNavigationFragmentTag), true);
        break;
      case CONTACTS_PERMISSION_DENIAL:
        this.navigator.navigateTo(AptoideApplication.getFragmentProvider()
            .newInviteFriendsFragment(
                InviteFriendsContract.View.OpenMode.CONTACTS_PERMISSION_DENIAL,
                exitNavigationFragmentTag), true);
        break;
      default:
        Logger.d(this.getClass()
            .getSimpleName(), "Wrong openMode type.");
    }
  }

  @Override public void showAboutFragment() {
    navigator.navigateTo(AptoideApplication.getFragmentProvider()
            .newDescriptionFragment(aboutFragmentActionBarTitle, aboutFragmentBodyMessage, "default"),
        true);
  }

  @Override public void showSuccessFragment(List<Contact> contacts) {
    navigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newSyncSuccessFragment(contacts, exitNavigationFragmentTag), true);
  }

  @Override public void navigateToThankYouConnectingFragment() {
    navigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newThankYouConnectingFragment(exitNavigationFragmentTag), true);
  }
}
