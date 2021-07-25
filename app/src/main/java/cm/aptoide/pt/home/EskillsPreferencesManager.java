package cm.aptoide.pt.home;

import cm.aptoide.pt.appview.PreferencesPersister;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.SHOW_ESKILLS_DIALOG;
import static cm.aptoide.pt.preferences.managed.ManagedKeys.SHOW_PROMOTIONS_DIALOG;

public class EskillsPreferencesManager {

  private PreferencesPersister preferencesPersister;

  public EskillsPreferencesManager(PreferencesPersister preferencesPersister) {
    this.preferencesPersister = preferencesPersister;
  }

  public boolean shouldShowPromotionsDialog() {
    return preferencesPersister.get(SHOW_ESKILLS_DIALOG, true);
  }

  public void setPromotionsDialogShown() {
    preferencesPersister.save(SHOW_ESKILLS_DIALOG, false);
  }
}
