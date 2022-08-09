package cm.aptoide.pt.aptoide_installer.data.download.filedownloader

import cm.aptoide.pt.downloadmanager.PathProvider
import cm.aptoide.pt.downloads_database.data.database.model.FileToDownload

class FilePathProvider(val apkPath: String, val obbPath: String, val cachePath: String) :
  PathProvider {

  override fun getFilePathFromFileType(fileToDownload: FileToDownload): String {
    return when (fileToDownload.fileType) {
      FileToDownload.APK -> apkPath
      FileToDownload.OBB -> obbPath + fileToDownload.packageName + "/"
      FileToDownload.SPLIT -> apkPath + fileToDownload.packageName + "-splits/"
      FileToDownload.GENERIC -> cachePath
      else -> cachePath
    }
  }

}