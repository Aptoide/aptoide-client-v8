package cm.aptoide.pt.preferences;

import rx.Completable;
import rx.Observable;

public class LocalPersistenceAdultContent implements AdultContent {

  private static final String ADULT_CONTENT_PIN_PREFERENCES_KEY = "Maturepin";
  private static final String ADULT_CONTENT_PREFERENCES_KEY = "matureChkBox";
  private final Preferences preferences;
  private final SecurePreferences securePreferences;

  public LocalPersistenceAdultContent(Preferences preferences,
      SecurePreferences securePreferences) {
    this.preferences = preferences;
    this.securePreferences = securePreferences;
  }

  @Override public Observable<Boolean> pinRequired() {
    return securePreferences.getInt(ADULT_CONTENT_PIN_PREFERENCES_KEY, -1)
        .map(value -> value != -1);
  }

  @Override public Completable requirePin(int pin) {
    return securePreferences.save(ADULT_CONTENT_PIN_PREFERENCES_KEY, pin);
  }

  @Override public Completable removePin(int pin) {
    return securePreferences.getInt(ADULT_CONTENT_PIN_PREFERENCES_KEY, -1)
        .first()
        .toSingle()
        .flatMapCompletable(savedPin -> {
          if (savedPin.equals(pin)) {
            return securePreferences.remove(ADULT_CONTENT_PIN_PREFERENCES_KEY);
          }
          return Completable.error(new SecurityException("Pin does not match."));
        });
  }

  @Override public Completable enable() {
    return preferences.save(ADULT_CONTENT_PREFERENCES_KEY, true);
  }

  @Override public Completable disable() {
    return preferences.save(ADULT_CONTENT_PREFERENCES_KEY, false);
  }

  @Override public Observable<Boolean> enabled() {
    return preferences.getBoolean(ADULT_CONTENT_PREFERENCES_KEY, false);
  }

  @Override public Completable enable(int pin) {
    return securePreferences.getInt(ADULT_CONTENT_PIN_PREFERENCES_KEY, -1)
        .first()
        .toSingle()
        .flatMapCompletable(savedPin -> {
          if (savedPin.equals(pin)) {
            return enable();
          }
          return Completable.error(new SecurityException("Pin does not match."));
        });
  }
}
