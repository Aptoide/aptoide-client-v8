package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.downloads_database.data.database.model.FileToDownload;

public interface PathProvider {

  String getFilePathFromFileType(FileToDownload fileToDownload);
}
