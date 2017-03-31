package cm.aptoide.pt.v8engine.preferences;

import cm.aptoide.accountmanager.AptoideAccountManager;
import rx.Completable;
import rx.Observable;

public class AdultContent {

  private static final String ADULT_CONTENT_PIN_PREFERENCES_KEY = "Maturepin";
  private static final String ADULT_CONTENT_PREFERENCES_KEY = "matureChkBox";
  private final AptoideAccountManager accountManager;
  private final Preferences preferences;
  private final SecurePreferences securePreferences;

  public AdultContent(AptoideAccountManager accountManager, Preferences preferences,
      SecurePreferences securePreferences) {
    this.accountManager = accountManager;
    this.preferences = preferences;
    this.securePreferences = securePreferences;
  }

  public Observable<Boolean> pinRequired() {
    return securePreferences.getInt(ADULT_CONTENT_PIN_PREFERENCES_KEY, -1)
        .map(value -> value != -1);
  }

  public Completable requirePin(int pin) {
    return securePreferences.save(ADULT_CONTENT_PIN_PREFERENCES_KEY, pin);
  }

  public Completable removePin(int pin) {
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

  public Completable enable() {
    return accountManager.updateAccount(true)
        .andThen(preferences.save(ADULT_CONTENT_PREFERENCES_KEY, true))
        .onErrorResumeNext(throwable -> preferences.save(ADULT_CONTENT_PREFERENCES_KEY, true));
  }

  public Completable disable() {
    return accountManager.updateAccount(false)
        .andThen(preferences.save(ADULT_CONTENT_PREFERENCES_KEY, false))
        .onErrorResumeNext(throwable -> preferences.save(ADULT_CONTENT_PREFERENCES_KEY, false));
  }

  public Observable<Boolean> enabled() {
    return Observable.combineLatest(accountManager.accountStatus(),
        preferences.getBoolean(ADULT_CONTENT_PREFERENCES_KEY, false),
        (account, isLocalAdultContentEnabled) -> {
          if (account.isLoggedIn()) {
            return account.isAdultContentEnabled();
          }
          return isLocalAdultContentEnabled;
        })
        .distinctUntilChanged()
        .flatMap(status -> preferences.save(ADULT_CONTENT_PREFERENCES_KEY, status)
            .andThen(Observable.just(status)));
  }

  public Completable enable(int pin) {
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
