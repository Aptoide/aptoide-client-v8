package cm.aptoide.pt.addressbook.view;

import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.presenter.AddressBookNavigation;

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
  private final String theme;

  public AddressBookNavigationManager(FragmentNavigator navigator, String exitNavigatonFragmentTag,
      String aboutFragmentActionBarTitle, String aboutFragmentBodyMessage, String theme) {
    this.navigator = navigator;
    this.exitNavigationFragmentTag = exitNavigatonFragmentTag;
    this.aboutFragmentActionBarTitle = aboutFragmentActionBarTitle;
    this.aboutFragmentBodyMessage = aboutFragmentBodyMessage;
    this.theme = theme;
  }

  @Override public void leaveAddressBook() {
    this.navigator.cleanBackStackUntil(exitNavigationFragmentTag);
  }

  @Override public void navigateToThankYouConnectingFragment() {
    navigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newThankYouConnectingFragment(exitNavigationFragmentTag), true);
  }
}
