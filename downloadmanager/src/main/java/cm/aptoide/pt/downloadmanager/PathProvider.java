package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomFileToDownload;

public interface PathProvider {

  String getFilePathFromFileType(RoomFileToDownload fileToDownload);
}
