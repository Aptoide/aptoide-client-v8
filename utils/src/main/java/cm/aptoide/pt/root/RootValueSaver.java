package cm.aptoide.pt.root;

import rx.Completable;
import rx.Single;

/**
 * Created by trinkes on 07/06/2017.
 */

public interface RootValueSaver {
  Single<Boolean> isPhoneRoot();

  Completable save(boolean rootAvailable);
}
