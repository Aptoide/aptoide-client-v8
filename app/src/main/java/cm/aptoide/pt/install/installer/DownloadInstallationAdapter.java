/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.install.installer;

import cm.aptoide.pt.database.room.RoomDownload;
import cm.aptoide.pt.database.room.RoomFileToDownload;
import cm.aptoide.pt.database.room.RoomInstalled;
import cm.aptoide.pt.downloadmanager.DownloadPersistence;
import cm.aptoide.pt.install.InstalledRepository;
import java.io.File;
import java.util.List;
import rx.Completable;

/**
 * Created by marcelobenites on 7/22/16.
 */
public class DownloadInstallationAdapter implements Installation {

  private final RoomDownload download;
  private DownloadPersistence downloadPersistence;
  private InstalledRepository ongoingInstallProvider;
  private RoomInstalled installed;

  public DownloadInstallationAdapter(RoomDownload download, DownloadPersistence downloadPersistence,
      InstalledRepository installedRepository, RoomInstalled installed) {
    this.download = download;
    this.downloadPersistence = downloadPersistence;
    this.ongoingInstallProvider = installedRepository;
    this.installed = installed;
  }

  @Override public String getId() {
    return download.getMd5();
  }

  @Override public String getPackageName() {
    return download.getFilesToDownload()
        .get(0)
        .getPackageName();
  }

  @Override public int getVersionCode() {
    return download.getFilesToDownload()
        .get(0)
        .getVersionCode();
  }

  @Override public String getVersionName() {
    return download.getVersionName();
  }

  @Override public File getFile() {
    return new File(download.getFilesToDownload()
        .get(0)
        .getFilePath());
  }

  @Override public Completable save() {
    return ongoingInstallProvider.save(installed);
  }

  @Override public int getStatus() {
    return installed.getStatus();
  }

  @Override public void setStatus(int status) {
    installed.setStatus(status);
  }

  @Override public int getType() {
    return installed.getType();
  }

  @Override public void setType(int type) {
    installed.setType(type);
  }

  @Override public List<RoomFileToDownload> getFiles() {
    return download.getFilesToDownload();
  }

  @Override public void saveFileChanges() {
    downloadPersistence.save(download);
  }
}
