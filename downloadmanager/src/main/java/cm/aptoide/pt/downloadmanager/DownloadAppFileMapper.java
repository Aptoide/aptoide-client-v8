package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.database.realm.FileToDownload;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.List;

import static cm.aptoide.pt.database.realm.FileToDownload.APK;
import static cm.aptoide.pt.database.realm.FileToDownload.GENERIC;
import static cm.aptoide.pt.database.realm.FileToDownload.OBB;

/**
 * Created by filipegoncalves on 9/12/18.
 */

public class DownloadAppFileMapper {

  public List<DownloadAppFile> mapFileToDownloadList(RealmList<FileToDownload> filesToDownload) {
    List<DownloadAppFile> downloadAppFileList = new ArrayList<>();
    for (FileToDownload fileToDownload : filesToDownload) {
      downloadAppFileList.add(
          new DownloadAppFile(fileToDownload.getLink(), fileToDownload.getAltLink(),
              fileToDownload.getMd5(), fileToDownload.getVersionCode(),
              fileToDownload.getPackageName(), fileToDownload.getFileName(),
              mapFileType(fileToDownload.getFileType())));
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
      default:
        throw new IllegalStateException("Invalid file type");
    }
    return type;
  }
}
