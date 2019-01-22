package cm.aptoide.pt.appview;

import cm.aptoide.pt.preferences.managed.ManagedKeys;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class PreferencesManager {

  private PreferencesPersister persister;

  public PreferencesManager(PreferencesPersister persister) {
    this.persister = persister;
  }

  public void increaseNotLoggedInInstallClicks() {
    int oldValue = persister.get(ManagedKeys.NOT_LOGGED_IN_NUMBER_OF_INSTALL_CLICKS, 0);
    persister.save(ManagedKeys.NOT_LOGGED_IN_NUMBER_OF_INSTALL_CLICKS, oldValue + 1);
  }

  public boolean shouldShowInstallRecommendsPreviewDialog() {
    return persister.get(ManagedKeys.DONT_SHOW_ME_AGAIN, true);
  }

  public boolean canShowNotLoggedInDialog() {
    return persister.get(ManagedKeys.NOT_LOGGED_IN_NUMBER_OF_INSTALL_CLICKS, 0) == 2
        || persister.get(ManagedKeys.NOT_LOGGED_IN_NUMBER_OF_INSTALL_CLICKS, 0) == 4;
  }

  public void setShouldShowInstallRecommendsPreviewDialog(boolean show) {
    persister.save(ManagedKeys.DONT_SHOW_ME_AGAIN, show);
  }
}
