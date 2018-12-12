package cm.aptoide.pt.promotions;

import cm.aptoide.pt.appview.PreferencesPersister;

import static cm.aptoide.pt.preferences.managed.ManagedKeys.SHOW_PROMOTIONS_DIALOG;

public class PromotionsPreferencesManager {

  private PreferencesPersister preferencesPersister;

  public PromotionsPreferencesManager(PreferencesPersister preferencesPersister) {
    this.preferencesPersister = preferencesPersister;
  }

  public boolean shouldShowPromotionsDialog() {
    return preferencesPersister.get(SHOW_PROMOTIONS_DIALOG, true);
  }

  public void dontShowPromotionsDialog() {
    preferencesPersister.save(SHOW_PROMOTIONS_DIALOG, false);
  }
}
