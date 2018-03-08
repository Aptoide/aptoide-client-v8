package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.updates.UpdateRepository;
import rx.Observable;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class UpdatesManager {
  private UpdateRepository updateRepository;

  //// TODO: 3/7/18 manage the interactions related with updates

  public Observable<UpdateApp> getUpdatesList() {
    return Observable.empty();
  }
}
