package cm.aptoide.pt.aptoide_installer.model

import cm.aptoide.pt.downloads_database.data.database.model.DownloadEntity
import cm.aptoide.pt.downloads_database.data.database.model.FileToDownload

class DownloadFactory {

  fun createDownload(download: Download): DownloadEntity {
    val downloadEntity = DownloadEntity()
    downloadEntity.md5 = download.md5
    downloadEntity.overallDownloadStatus = DownloadEntity.INVALID_STATUS
    downloadEntity.icon = download.icon
    downloadEntity.appName = download.appName
    downloadEntity.packageName = download.packageName
    downloadEntity.attributionId = "to be implemented"
    downloadEntity.setHasAppc(download.hasAppc)
    downloadEntity.versionCode = download.versionCode
    downloadEntity.versionName = download.versionName
    downloadEntity.size = calculateAppSize(download.appSize, download.downloadFileList)
    downloadEntity.trustedBadge = download.trustedBadge
    downloadEntity.storeName = download.storeName
    downloadEntity.action = DownloadEntity.ACTION_INSTALL
    downloadEntity.filesToDownload = getFilesToDownloadList(download.downloadFileList)
    return downloadEntity
  }

  private fun getFilesToDownloadList(downloadFileList: List<DownloadFile>): List<FileToDownload> {
    val filesToDownload = ArrayList<FileToDownload>()

    for (downloadFile in downloadFileList) {
      filesToDownload.add(
        FileToDownload.createFileToDownload(
          downloadFile.path,
          downloadFile.altPath,
          downloadFile.md5,
          downloadFile.fileName,
          mapFileType(downloadFile.fileType),
          downloadFile.packageName,
          downloadFile.versionCode,
          downloadFile.versionName, downloadFile.cachePath, mapFileSubType(downloadFile.subFileType)
        )
      )
    }

    return filesToDownload
  }

  private fun mapFileType(fileType: FileType): Int {
    return when (fileType) {
      FileType.APK -> {
        FileToDownload.APK
      }
      FileType.OBB -> {
        FileToDownload.OBB
      }
      FileType.SPLIT -> {
        FileToDownload.SPLIT
      }
    }
  }

  private fun mapFileSubType(subFileType: SubFileType): Int {
    return when (subFileType) {
      SubFileType.MAIN -> {
        FileToDownload.MAIN
      }
      SubFileType.PATCH -> {
        FileToDownload.PATCH
      }
      SubFileType.ASSET -> {
        FileToDownload.ASSET
      }
      SubFileType.FEATURE -> {
        FileToDownload.FEATURE
      }
      SubFileType.BASE -> {
        FileToDownload.BASE
      }
      SubFileType.SUBTYPE_APK -> {
        FileToDownload.SUBTYPE_APK
      }
    }
  }

  private fun calculateAppSize(appSize: Long, downloadFileList: List<DownloadFile>): Long {
    return appSize
  }
}