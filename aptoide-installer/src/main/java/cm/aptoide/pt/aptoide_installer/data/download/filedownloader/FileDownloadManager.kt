package cm.aptoide.pt.aptoide_installer.data.download.filedownloader

import cm.aptoide.pt.downloadmanager.Constants
import cm.aptoide.pt.downloadmanager.FileDownloadCallback
import cm.aptoide.pt.downloadmanager.FileDownloader
import io.reactivex.Completable
import io.reactivex.Observable

class FileDownloadManager(
  private val fileDownloader: com.liulishuo.filedownloader.FileDownloader,
  private val fileDownloadTask: FileDownloadTask,
  private val downloadsPath: String,
  private val mainDownloadPath: String,
  private val fileType: Int,
  private val packageName: String,
  private val versionCode: Int,
  private val fileName: String
) : FileDownloader {

  companion object {
    const val RETRY_TIMES = 3
    const val PROGRESS_MAX_VALUE = 100
    private const val APTOIDE_DOWNLOAD_TASK_TAG_KEY = 888
  }

  private var downloadId = 0


  override fun startFileDownload(): Completable {
    return Completable.fromAction {
      if (mainDownloadPath.isNullOrEmpty()) {
        throw IllegalArgumentException("The url for the download can not be empty")
      } else {
        setupBaseDownloadTask(mainDownloadPath, versionCode, packageName, fileType, fileName)
        fileDownloader.start(fileDownloadTask, true)
      }
    }
  }

  override fun removeDownloadFile(): Completable {
    return Completable.fromAction {
      fileDownloader.clear(
        downloadId,
        mainDownloadPath
      )
    }
  }

  override fun observeFileDownloadProgress(): Observable<FileDownloadCallback> {
    return fileDownloadTask.onDownloadStateChanged()
  }

  override fun stopFailedDownload() {
    val taskId = fileDownloader.replaceListener(downloadId, null)
    if (taskId != 0) {
      fileDownloader.pause(taskId)
    }
  }

  private fun setupBaseDownloadTask(
    mainDownloadPath: String, versionCode: Int, packageName: String,
    fileType: Int, fileName: String
  ) {
    val baseDownloadTask = fileDownloader.create(mainDownloadPath)
    baseDownloadTask.autoRetryTimes = RETRY_TIMES
    baseDownloadTask.addHeader(Constants.VERSION_CODE, versionCode.toString())
    baseDownloadTask.addHeader(Constants.PACKAGE, packageName)
    baseDownloadTask.addHeader(Constants.FILE_TYPE, fileType.toString())
    baseDownloadTask.setTag(APTOIDE_DOWNLOAD_TASK_TAG_KEY, fileDownloadTask)
    baseDownloadTask.listener = fileDownloadTask
    baseDownloadTask.callbackProgressTimes = PROGRESS_MAX_VALUE
    baseDownloadTask.path = downloadsPath + fileName
    downloadId = baseDownloadTask.asInQueueTask()
      .enqueue()
  }
}