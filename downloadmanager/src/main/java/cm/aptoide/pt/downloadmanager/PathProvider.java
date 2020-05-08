package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.FileToDownload;

public interface PathProvider {

  String getFilePathFromFileType(FileToDownload fileToDownload);
}
