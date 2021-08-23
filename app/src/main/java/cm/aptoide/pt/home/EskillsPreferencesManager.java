package cm.aptoide.pt.home;

import cm.aptoide.pt.appview.PreferencesPersister;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.SHOW_ESKILLS_DIALOG;

public class EskillsPreferencesManager {

  private final PreferencesPersister preferencesPersister;

  public EskillsPreferencesManager(PreferencesPersister preferencesPersister) {
    this.preferencesPersister = preferencesPersister;
  }

  public boolean shouldShowEskillsDialog() {
    return preferencesPersister.get(SHOW_ESKILLS_DIALOG, true);
  }

  public void setEskillsDialogShown() {
    preferencesPersister.save(SHOW_ESKILLS_DIALOG, false);
  }
}
