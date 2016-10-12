/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.preferences.secure;

/**
 * Created by neuro on 21-04-2016.
 */
public class SecurePreferences {

  private static final String TAG = SecurePreferences.class.getSimpleName();

  /**
   * DO NOT USE THIS METHOD
   */
  public static void putString(String key, String value) {
    SecurePreferencesImplementation.getInstance().edit().putString(key, value).apply();
  }

  /**
   * DO NOT USE THIS METHOD!!!!
   */
  public static String getString(String key) {
    return SecurePreferencesImplementation.getInstance().getString(key, null);
  }

  public static void remove(String key) {
    SecurePreferencesImplementation.getInstance().edit().remove(key).apply();
  }

  /**
   * DO NOT USE THIS METHOD
   */
  public static void putBoolean(String key, boolean value) {
    SecurePreferencesImplementation.getInstance().edit().putBoolean(key, value).apply();
  }

  /**
   * DO NOT USE THIS METHOD!!!!
   */
  public static boolean getBoolean(String key) {
    return SecurePreferencesImplementation.getInstance().getBoolean(key, false);
  }

  public static boolean isUserDataLoaded() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.USER_DATA_LOADED, false);
  }

  public static void setUserDataLoaded() {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.USER_DATA_LOADED, true)
        .apply();
  }

  public static boolean isFirstRun() {
    return SecurePreferencesImplementation.getInstance().getBoolean(SecureKeys.FIRST_RUN, true);
  }

  public static void setFirstRun(boolean b) {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.FIRST_RUN, b)
        .apply();
  }

  public static boolean isWizardAvailable() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.WIZARD_AVAILABLE, false);
  }

  public static void setWizardAvailable(boolean available) {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.WIZARD_AVAILABLE, available)
        .apply();
  }

  public static int getAdultContentPin() {
    return SecurePreferencesImplementation.getInstance().getInt(SecureKeys.ADULT_CONTENT_PIN, -1);
  }

  public static void setAdultContentPin(int pin) {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putInt(SecureKeys.ADULT_CONTENT_PIN, pin)
        .apply();
  }

  public static boolean isTimelineActive() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.IS_TIMELINE_ACTIVE, false);
  }

  public static int getMatureSwitch() {
    return isAdultSwitchActive() ? 1 : 0;
  }

  public static void setAdultSwitch(boolean active) {
    SecurePreferencesImplementation.getInstance()
        .edit()
        .putBoolean(SecureKeys.ADULT_CONTENT_SWITCH, active)
        .apply();
  }

  public static boolean isAdultSwitchActive() {
    return SecurePreferencesImplementation.getInstance()
        .getBoolean(SecureKeys.ADULT_CONTENT_SWITCH, false);
  }
}
