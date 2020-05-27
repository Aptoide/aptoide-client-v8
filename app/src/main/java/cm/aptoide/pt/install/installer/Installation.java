/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.install.installer;

import cm.aptoide.pt.database.room.RoomFileToDownload;
import java.io.File;
import java.util.List;
import rx.Completable;

/**
 * Created by marcelobenites on 7/22/16.
 */
public interface Installation {

  /**
   * @return installation MD5 sum
   */
  String getId();

  String getPackageName();

  int getVersionCode();

  String getVersionName();

  File getFile();

  Completable save();

  int getStatus();

  void setStatus(int status);

  int getType();

  void setType(int type);

  List<RoomFileToDownload> getFiles();

  void saveFileChanges();
}
