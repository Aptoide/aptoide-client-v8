package cm.aptoide.pt.preferences;

import cm.aptoide.accountmanager.AdultContent;
import rx.Completable;
import rx.Observable;

/**
 * Created by trinkes on 16/01/2018.
 */

public class AdultContentManager implements AdultContent {

  public AdultContentManager() {
  }

  @Override public Observable<Boolean> pinRequired() {
    return Observable.just(false);
  }

  @Override public Completable requirePin(int pin) {
    return Completable.complete();
  }

  @Override public Completable removePin(int pin) {
    return Completable.complete();
  }

  @Override public Completable enable(boolean isLogged) {
    return Completable.complete();
  }

  @Override public Completable disable(boolean isLogged) {
    return Completable.complete();
  }

  @Override public Observable<Boolean> enabled() {
    return Observable.just(false);
  }

  @Override public Completable enable(int pin) {
    return Completable.complete();
  }
}
