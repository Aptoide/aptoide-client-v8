package cm.aptoide.pt.downloadmanager.interfaces;

import cm.aptoide.pt.database.realm.Download;

/**
 * Created by trinkes on 04/01/2017.
 */

public interface Analytics {
  void onError(Download download, Throwable throwable);
}
