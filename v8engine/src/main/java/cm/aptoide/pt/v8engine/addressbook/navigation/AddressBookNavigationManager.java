package cm.aptoide.pt.v8engine.addressbook.navigation;

import cm.aptoide.pt.navigation.NavigationManagerV4;

/**
 * Created by jdandrade on 02/03/2017.
 */

public class AddressBookNavigationManager implements AddressBookNavigation {

  private final NavigationManagerV4 navigationManager;
  private final String exitNavigationFragmentTag;

  public AddressBookNavigationManager(NavigationManagerV4 navigationManager,
      String exitNavigatonFragmentTag) {
    this.navigationManager = navigationManager;
    this.exitNavigationFragmentTag = exitNavigatonFragmentTag;
  }

  @Override public void exitAddressBook() {
    this.navigationManager.cleanBackStackUntil(exitNavigationFragmentTag);
  }
}
