/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.v8engine.install.rollback;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.v8engine.install.installer.RollbackInstallation;
import java.io.File;
import java.util.List;

/**
 * Created by marcelobenites on 7/22/16.
 */
public class DownloadInstallationAdapter implements RollbackInstallation {

  private final Download download;
  private DownloadAccessor downloadAccessor;

  public DownloadInstallationAdapter(Download download, DownloadAccessor downloadAccessor) {
    this.download = download;
    this.downloadAccessor = downloadAccessor;
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

  @Override public String getAppName() {
    return download.getAppName();
  }

  @Override public String getIcon() {
    return download.getIcon();
  }

  @Override public String downloadLink() {
    return download.getFilesToDownload()
        .get(0)
        .getLink();
  }

  @Override public String getAltDownloadLink() {
    return download.getFilesToDownload()
        .get(0)
        .getAltLink();
  }

  @Override public String getMainObbName() {
    if (download.getFilesToDownload()
        .size() > 1
        && download.getFilesToDownload()
        .get(1)
        .getFileType() == FileToDownload.OBB) {
      return download.getFilesToDownload()
          .get(1)
          .getFileName();
    } else {
      return null;
    }
  }

  @Override public String getPatchObbPath() {
    if (download.getFilesToDownload()
        .size() > 2
        && download.getFilesToDownload()
        .get(2)
        .getFileType() == FileToDownload.OBB) {
      return download.getFilesToDownload()
          .get(2)
          .getLink();
    } else {
      return null;
    }
  }

  @Override public String getPatchObbName() {
    if (download.getFilesToDownload()
        .size() > 2
        && download.getFilesToDownload()
        .get(2)
        .getFileType() == FileToDownload.OBB) {
      return download.getFilesToDownload()
          .get(2)
          .getFileName();
    } else {
      return null;
    }
  }

  @Override public String getMainObbPath() {
    if (download.getFilesToDownload()
        .size() > 1
        && download.getFilesToDownload()
        .get(1)
        .getFileType() == FileToDownload.OBB) {
      return download.getFilesToDownload()
          .get(1)
          .getPath();
    } else {
      return null;
    }
  }

  @Override public List<FileToDownload> getFiles() {
    return download.getFilesToDownload();
  }

  @Override public long getTimeStamp() {
    return download.getTimeStamp();
  }

  @Override public void save() {
    downloadAccessor.save(download);
  }
}
