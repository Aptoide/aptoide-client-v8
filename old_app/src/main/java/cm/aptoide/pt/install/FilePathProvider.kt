package cm.aptoide.pt.install

import cm.aptoide.pt.database.room.RoomFileToDownload
import cm.aptoide.pt.downloadmanager.PathProvider

class FilePathProvider(val apkPath: String, val obbPath: String, val cachePath: String) :
    PathProvider {

  override fun getFilePathFromFileType(fileToDownload: RoomFileToDownload): String? {
    return when (fileToDownload.fileType) {
      RoomFileToDownload.APK -> apkPath
      RoomFileToDownload.OBB -> obbPath + fileToDownload.packageName + "/"
      RoomFileToDownload.SPLIT -> apkPath + fileToDownload.packageName + "-splits/"
      RoomFileToDownload.GENERIC -> cachePath
      else -> cachePath
    }
  }

}