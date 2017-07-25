package cm.aptoide.pt.root;

import rx.Completable;
import rx.Single;

/**
 * Created by trinkes on 19/05/2017.
 */

public class RootAvailabilityManager {

  private RootValueSaver rootValueSaver;

  public RootAvailabilityManager(RootValueSaver rootValueSaver) {
    this.rootValueSaver = rootValueSaver;
  }

  public Single<Boolean> isRootAvailable() {
    return rootValueSaver.isPhoneRoot();
  }

  public Completable updateRootAvailability() {
    return rootValueSaver.save(RootShell.isRootAvailable());
  }
}
