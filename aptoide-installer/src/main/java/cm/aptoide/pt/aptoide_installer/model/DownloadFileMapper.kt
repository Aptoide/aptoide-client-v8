package cm.aptoide.pt.aptoide_installer.model

import cm.aptoide.pt.downloads_database.data.database.model.FileToDownload

class DownloadFileMapper {

  fun mapDownloadFileList(fileToDownloadList: List<FileToDownload>): List<DownloadFile> {
    val downloadFileList = ArrayList<DownloadFile>()
    for (fileToDownload in fileToDownloadList) {
      downloadFileList.add(mapDownloadFile(fileToDownload))
    }
    return downloadFileList
  }

  fun mapDownloadFile(fileToDownload: FileToDownload): DownloadFile {
    return DownloadFile(
      fileToDownload.md5,
      fileToDownload.link,
      fileToDownload.altLink,
      fileToDownload.packageName,
      fileToDownload.versionCode,
      fileToDownload.versionName,
      fileToDownload.fileName,
      mapFileType(fileToDownload.fileType),
      mapFileSubType(fileToDownload.subFileType), fileToDownload.filePath
    )
  }

  private fun mapFileSubType(subFileType: Int): SubFileType {
    return when (subFileType) {
      FileToDownload.ASSET -> {
        SubFileType.ASSET
      }
      FileToDownload.BASE -> {
        SubFileType.BASE
      }
      FileToDownload.FEATURE -> {
        SubFileType.FEATURE
      }
      FileToDownload.SUBTYPE_APK -> {
        SubFileType.SUBTYPE_APK
      }
      FileToDownload.MAIN -> {
        SubFileType.MAIN
      }
      FileToDownload.PATCH -> {
        SubFileType.PATCH
      }
      else -> {
        SubFileType.SUBTYPE_APK
      }
    }
  }

  private fun mapFileType(fileType: Int): FileType {
    return when (fileType) {
      FileToDownload.APK -> {
        FileType.APK
      }
      FileToDownload.OBB -> {
        FileType.OBB
      }
      FileToDownload.SPLIT -> {
        FileType.SPLIT
      }
      else -> {
        FileType.APK
      }
    }
  }
}