package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.room.RoomFileToDownload;
import java.util.ArrayList;
import java.util.List;

import static cm.aptoide.pt.database.room.RoomFileToDownload.APK;
import static cm.aptoide.pt.database.room.RoomFileToDownload.GENERIC;
import static cm.aptoide.pt.database.room.RoomFileToDownload.OBB;
import static cm.aptoide.pt.database.room.RoomFileToDownload.SPLIT;

/**
 * Created by filipegoncalves on 9/12/18.
 */

public class DownloadAppFileMapper {

  public List<DownloadAppFile> mapFileToDownloadList(List<RoomFileToDownload> filesToDownload) {
    List<DownloadAppFile> downloadAppFileList = new ArrayList<>();
    for (RoomFileToDownload roomFileToDownload : filesToDownload) {
      downloadAppFileList.add(
          new DownloadAppFile(roomFileToDownload.getLink(), roomFileToDownload.getAltLink(),
              roomFileToDownload.getMd5(), roomFileToDownload.getVersionCode(),
              roomFileToDownload.getPackageName(), roomFileToDownload.getFileName(),
              mapFileType(roomFileToDownload.getFileType())));
    }
    return downloadAppFileList;
  }

  private DownloadAppFile.FileType mapFileType(int fileType) {
    DownloadAppFile.FileType type;
    switch (fileType) {
      case APK:
        type = DownloadAppFile.FileType.APK;
        break;
      case OBB:
        type = DownloadAppFile.FileType.OBB;
        break;
      case GENERIC:
        type = DownloadAppFile.FileType.GENERIC;
        break;
      case SPLIT:
        type = DownloadAppFile.FileType.SPLIT;
        break;
      default:
        throw new IllegalStateException("Invalid file type");
    }
    return type;
  }
}
