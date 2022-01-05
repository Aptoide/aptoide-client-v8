package cm.aptoide.pt.install

import android.os.Build
import android.os.Environment
import android.os.StatFs
import cm.aptoide.pt.database.room.RoomDownload
import cm.aptoide.pt.utils.FileUtils

class InstallAppSizeValidator(val filePathProvider: FilePathProvider) {

  fun hasEnoughSpaceToInstallApp(download: RoomDownload): Boolean {
    return if (isAppAlreadyDownloaded(download)) {
      true
    } else {
      download.size <= getAvailableSpace()
    }
  }

  fun getAvailableSpace(): Long {
    val stat = StatFs(Environment.getDataDirectory().path)
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
      (stat.availableBlocks * stat.blockSize).toLong()
    } else {
      stat.availableBlocksLong * stat.blockSizeLong
    }
  }


  private fun isAppAlreadyDownloaded(download: RoomDownload): Boolean {
    if (download.filesToDownload.isEmpty()) {
      return false
    } else {
      for (fileToDownload in download.filesToDownload) {
        if (!FileUtils.fileExists(
                filePathProvider.getFilePathFromFileType(
                    fileToDownload) + fileToDownload.fileName)) {
          return false
        }
      }
      return true
    }

  }
}