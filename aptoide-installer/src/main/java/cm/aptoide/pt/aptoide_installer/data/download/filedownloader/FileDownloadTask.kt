package cm.aptoide.pt.aptoide_installer.data.download.filedownloader

import cm.aptoide.pt.downloadmanager.AppDownloadStatus
import cm.aptoide.pt.downloadmanager.FileDownloadCallback
import cm.aptoide.pt.downloadmanager.FileDownloadProgressResult
import cm.aptoide.pt.logger.Logger
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadLargeFileListener
import com.liulishuo.filedownloader.exception.FileDownloadHttpException
import com.liulishuo.filedownloader.exception.FileDownloadOutOfSpaceException
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class FileDownloadTask(
  private val downloadStatus: PublishSubject<FileDownloadCallback>,
  private val md5: String,
  private val md5Comparator: Md5Comparator,
  private val fileName: String,
  private val attributionId: String?
) : FileDownloadLargeFileListener() {

  override fun pending(
    baseDownloadTask: BaseDownloadTask,
    soFarBytes: Long,
    totalBytes: Long
  ) {
    downloadStatus.onNext(
      FileDownloadTaskStatus(
        AppDownloadStatus.AppDownloadState.PENDING,
        FileDownloadProgressResult(soFarBytes, totalBytes), md5
      )
    )
  }

  override fun progress(
    baseDownloadTask: BaseDownloadTask,
    soFarBytes: Long,
    totalBytes: Long
  ) {
    downloadStatus.onNext(
      FileDownloadTaskStatus(
        AppDownloadStatus.AppDownloadState.PROGRESS,
        FileDownloadProgressResult(soFarBytes, totalBytes), md5
      )
    )
  }

  override fun paused(
    baseDownloadTask: BaseDownloadTask,
    soFarBytes: Long,
    totalBytes: Long
  ) {
    downloadStatus.onNext(
      FileDownloadTaskStatus(
        AppDownloadStatus.AppDownloadState.PAUSED,
        FileDownloadProgressResult(soFarBytes, totalBytes), md5
      )
    )
  }

  override fun completed(baseDownloadTask: BaseDownloadTask) {
    Thread {
      val fileDownloadTaskStatus1 = FileDownloadTaskStatus(
        AppDownloadStatus.AppDownloadState.VERIFYING_FILE_INTEGRITY,
        FileDownloadProgressResult(
          baseDownloadTask.largeFileTotalBytes,
          baseDownloadTask.largeFileTotalBytes
        ), md5
      )
      downloadStatus.onNext(fileDownloadTaskStatus1)
      val fileDownloadTaskStatus: FileDownloadTaskStatus
      if (attributionId != null || md5Comparator.compareMd5(md5, fileName)) {
        fileDownloadTaskStatus = FileDownloadTaskStatus(
          AppDownloadStatus.AppDownloadState.COMPLETED,
          FileDownloadProgressResult(
            baseDownloadTask.largeFileTotalBytes,
            baseDownloadTask.largeFileTotalBytes
          ), md5
        )
        Logger.getInstance()
          .d(TAG, " Download completed")
      } else {
        Logger.getInstance()
          .d(TAG, " Download error in md5")
        fileDownloadTaskStatus = FileDownloadTaskStatus(
          AppDownloadStatus.AppDownloadState.ERROR_MD5_DOES_NOT_MATCH,
          md5, Md5DownloadComparisonException("md5 does not match")
        )
      }
      downloadStatus.onNext(fileDownloadTaskStatus)
    }.start()
  }

  override fun error(baseDownloadTask: BaseDownloadTask, error: Throwable) {
    val fileDownloadTaskStatus: FileDownloadTaskStatus = if (error != null) {
      error.printStackTrace()
      if (error is FileDownloadHttpException
        && error.code == FILE_NOT_FOUND_HTTP_ERROR
      ) {
        Logger.getInstance()
          .d(TAG, "File not found error on app: $md5")
        FileDownloadTaskStatus(
          AppDownloadStatus.AppDownloadState.ERROR_FILE_NOT_FOUND, md5,
          error
        )
      } else if (error is FileDownloadOutOfSpaceException) {
        Logger.getInstance()
          .d(TAG, "Out of space error for the app: $md5")
        FileDownloadTaskStatus(
          AppDownloadStatus.AppDownloadState.ERROR_NOT_ENOUGH_SPACE,
          md5, error
        )
      } else {
        Logger.getInstance()
          .d(TAG, "Generic error on app: $md5")
        FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.ERROR, md5, error)
      }
    } else {
      Logger.getInstance()
        .d(TAG, "Unknown error on app: $md5")
      FileDownloadTaskStatus(
        AppDownloadStatus.AppDownloadState.ERROR, md5,
        GeneralDownloadErrorException("Empty download error")
      )
    }
    downloadStatus.onNext(fileDownloadTaskStatus)
  }

  override fun warn(baseDownloadTask: BaseDownloadTask) {
    downloadStatus.onNext(
      FileDownloadTaskStatus(AppDownloadStatus.AppDownloadState.WARN, md5, null)
    )
  }

  fun onDownloadStateChanged(): Observable<FileDownloadCallback> {
    return downloadStatus
  }

  companion object {
    private const val FILE_NOT_FOUND_HTTP_ERROR = 404
    private const val TAG = "FileDownloader"
  }
}