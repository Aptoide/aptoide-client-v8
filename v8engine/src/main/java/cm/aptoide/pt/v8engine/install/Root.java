package cm.aptoide.pt.v8engine.install;

import cm.aptoide.pt.root.RootShell;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import rx.Completable;
import rx.Single;

/**
 * Created by trinkes on 19/05/2017.
 */

public class Root {
  public static final String IS_PHONE_ROOTED = "IS_PHONE_ROOTED";
  private SecurePreferences sharedPreferences;

  public Root(SecurePreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public Single<Boolean> isRootAvailable() {
    return Single.defer(() -> sharedPreferences.contains(IS_PHONE_ROOTED))
        .flatMap(contains -> {
          if (contains) {
            return sharedPreferences.getBoolean(IS_PHONE_ROOTED, false)
                .first()
                .toSingle();
          } else {
            return updateRootAvailability().andThen(isRootAvailable());
          }
        });
  }

  public Completable updateRootAvailability() {
    return sharedPreferences.save(IS_PHONE_ROOTED, RootShell.isRootAvailable());
  }
}
