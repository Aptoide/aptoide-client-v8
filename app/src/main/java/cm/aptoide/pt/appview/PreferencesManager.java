package cm.aptoide.pt.appview;

import cm.aptoide.pt.preferences.managed.ManagedKeys;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class PreferencesManager {

  private UserPreferencesPersister persister;

  public PreferencesManager(UserPreferencesPersister persister) {
    this.persister = persister;
  }

  public void setNotLoggedInInstallClicks() {
    int oldValue = persister.get(ManagedKeys.NOT_LOGGED_IN_NUMBER_OF_INSTALL_CLICKS, 0);
    persister.save(ManagedKeys.NOT_LOGGED_IN_NUMBER_OF_INSTALL_CLICKS, oldValue + 1);
  }
}
