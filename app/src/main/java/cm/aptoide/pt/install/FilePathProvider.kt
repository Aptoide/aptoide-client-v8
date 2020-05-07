package cm.aptoide.pt.install

import cm.aptoide.pt.database.realm.FileToDownload
import cm.aptoide.pt.downloadmanager.PathProvider

class FilePathProvider(val apkPath: String, val obbPath: String, val cachePath: String) :
    PathProvider {

  override fun getFilePathFromFileType(fileToDownload: FileToDownload): String? {
    return when (fileToDownload.fileType) {
      FileToDownload.APK -> apkPath
      FileToDownload.OBB -> obbPath + fileToDownload.packageName + "/"
      FileToDownload.SPLIT -> apkPath + fileToDownload.packageName + "-splits/"
      FileToDownload.GENERIC -> cachePath
      else -> cachePath
    }
  }

}