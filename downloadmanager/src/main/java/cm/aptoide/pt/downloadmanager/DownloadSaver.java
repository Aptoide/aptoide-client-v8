package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.Download;

/**
 * Created by trinkes on 25/05/2017.
 */

interface DownloadSaver {
  void save(Download download);
}
