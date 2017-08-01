/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.install.installer;

import cm.aptoide.pt.database.realm.FileToDownload;
import java.util.List;

/**
 * Created by trinkes on 9/8/16.
 */
public interface RollbackInstallation extends Installation {

  String getAppName();

  String getIcon();

  List<FileToDownload> getFiles();

  void saveFileChanges();
}
