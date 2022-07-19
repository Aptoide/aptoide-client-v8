package cm.aptoide.pt.aptoide_installer.data.download.filedownloader

import cm.aptoide.pt.downloadmanager.FileDownloadCallback
import cm.aptoide.pt.downloadmanager.FileDownloaderProvider
import com.liulishuo.filedownloader.FileDownloader
import io.reactivex.subjects.PublishSubject


class FileDownloadManagerProvider(
  private val downloadsPath: String,
  private val fileDownloader: FileDownloader, private val md5Comparator: Md5Comparator
) : FileDownloaderProvider {

  override fun createFileDownloader(
    md5: String?, mainDownloadPath: String?, fileType: Int,
    packageName: String?, versionCode: Int, fileName: String?,
    downloadStatusCallback: PublishSubject<FileDownloadCallback>, attributionId: String?
  ): cm.aptoide.pt.downloadmanager.FileDownloader {
    return FileDownloadManager(
      fileDownloader,
      FileDownloadTask(downloadStatusCallback, md5!!, md5Comparator, fileName!!, attributionId),
      downloadsPath, mainDownloadPath!!, fileType, packageName!!, versionCode, fileName
    )
  }
}