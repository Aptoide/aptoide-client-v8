package cm.aptoide.pt.preferences;

import cm.aptoide.accountmanager.AptoideAccountManager;
import rx.Completable;
import rx.Observable;

public class RemotePersistenceAdultContent implements AdultContent {
  private final AdultContent adultContent;
  private final AptoideAccountManager accountManager;

  public RemotePersistenceAdultContent(AdultContent adultContent,
      AptoideAccountManager accountManager) {
    this.adultContent = adultContent;
    this.accountManager = accountManager;
  }

  @Override public Observable<Boolean> pinRequired() {
    return adultContent.pinRequired();
  }

  @Override public Completable requirePin(int pin) {
    return adultContent.requirePin(pin);
  }

  @Override public Completable removePin(int pin) {
    return adultContent.removePin(pin);
  }

  @Override public Completable enable() {
    return accountManager.updateAccount(true)
        .andThen(adultContent.enable())
        .onErrorResumeNext(__ -> adultContent.enable());
  }

  @Override public Completable disable() {
    return accountManager.updateAccount(false)
        .andThen(adultContent.disable())
        .onErrorResumeNext(__ -> adultContent.disable());
  }

  @Override public Observable<Boolean> enabled() {
    return adultContent.enabled();
  }

  @Override public Completable enable(int pin) {
    return adultContent.enable(pin);
  }
}
