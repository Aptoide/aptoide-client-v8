package cm.aptoide.pt.preferences.secure;

import cm.aptoide.pt.annotation.Partners;

/**
 * Created by neuro on 21-04-2016.
 */
public class SecurePreferences {

  public static boolean shouldRunApkFy() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.SHOULD_RUN_APK_FY, true);
  }

  public static void setApkFyRun() {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.SHOULD_RUN_APK_FY, false)
        .apply();
  }

  public static boolean isFirstRun() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.FIRST_RUN, true);
  }

  public static void setFirstRun(boolean b) {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.FIRST_RUN, b)
        .apply();
  }

  public static boolean isWizardAvailable() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.WIZARD_AVAILABLE, true);
  }

  @Partners public static void setWizardAvailable(boolean available) {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.WIZARD_AVAILABLE, available)
        //.apply();
        .commit();
  }

  public static boolean isRootDialogShowed() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.ROOT_DIALOG_ShOWED, false);
  }

  public static void setRootDialogShowed(boolean displayed) {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.ROOT_DIALOG_ShOWED, displayed)
        .apply();
  }
}
