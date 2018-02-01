package cm.aptoide.pt.preferences;

import cm.aptoide.accountmanager.AccountService;
import cm.aptoide.accountmanager.AdultContent;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 16/01/2018.
 */

public class AdultContentManager implements AdultContent {
  private final LocalPersistenceAdultContent localContent;
  private final AccountService accountService;

  public AdultContentManager(LocalPersistenceAdultContent localContent,
      AccountService accountService) {
    this.localContent = localContent;
    this.accountService = accountService;
  }

  @Override public Observable<Boolean> pinRequired() {
    return localContent.pinRequired();
  }

  @Override public Completable requirePin(int pin) {
    return localContent.requirePin(pin);
  }

  @Override public Completable removePin(int pin) {
    return localContent.removePin(pin);
  }

  @Override public Completable enable(boolean isLogged) {
    if (isLogged) {
      return accountService.updateAccount(true)
          .andThen(localContent.enable());
    }
    return localContent.enable();
  }

  @Override public Completable disable(boolean isLogged) {
    if (isLogged) {
      return accountService.updateAccount(false)
          .andThen(localContent.disable());
    }
    return localContent.disable();
  }

  @Override public Observable<Boolean> enabled() {
    return localContent.enabled();
  }

  @Override public Completable enable(int pin) {
    return localContent.enable(pin);
  }
}
