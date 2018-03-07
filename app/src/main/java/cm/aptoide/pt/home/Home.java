package cm.aptoide.pt.home;

import java.util.Collections;
import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class Home {
  public Single<List<AppBundle>> getHomeBundles() {
    return Single.just(Collections.emptyList());
  }
}
