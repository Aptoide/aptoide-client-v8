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
      fileToDownload.packageName
    )
  }
}