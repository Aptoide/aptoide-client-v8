package cm.aptoide.pt.v8engine.install;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.install.root.RootShell;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import rx.Completable;
import rx.Single;

/**
 * Created by trinkes on 19/05/2017.
 */

public class Root {
  public static final String IS_PHONE_ROOTED = "IS_PHONE_ROOTED";
  private static final String TAG = Root.class.getSimpleName();
  private SecurePreferences sharedPreferences;

  public Root(SecurePreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public Single<Boolean> isRootAvailable() {
    return sharedPreferences.contains(IS_PHONE_ROOTED)
        .flatMap(contains -> {
          Logger.d(TAG, "isRootAvailable: Contains" + contains);
          if (contains) {
            return sharedPreferences.getBoolean(IS_PHONE_ROOTED, false)
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
