package cm.aptoide.pt.installer.fetch

import android.content.Context
import androidx.core.net.toFile
import androidx.core.net.toUri
import cm.aptoide.pt.aptoide_network.di.DownloadsOKHttp
import cm.aptoide.pt.extensions.checkMd5
import cm.aptoide.pt.install_manager.AbortException
import cm.aptoide.pt.install_manager.DownloadInfo
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.install_manager.dto.hasObb
import cm.aptoide.pt.install_manager.workers.PackageDownloader
import cm.aptoide.pt.installer.InstallerWorker
import cm.aptoide.pt.installer.di.DownloadsPath
import cm.aptoide.pt.installer.platform.InstallPermissions
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Downloader
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class FetchDownloader @Inject constructor(
  @ApplicationContext private val context: Context,
  private val installPermissions: InstallPermissions,
  @DownloadsPath private val downloadsPath: File,
  @DownloadsOKHttp private val okHttpClient: OkHttpClient
) : PackageDownloader {

  companion object {
    const val CONCURRENT_DOWNLOAD_LIMIT = 5
    const val RETRY_TIMES = 3
    const val VERSION_CODE = "versioncode"
    const val PACKAGE = "package"
    const val FILE_TYPE = "fileType"
  }

  private val fetch: Fetch
  private val contentHashes = mutableMapOf<Int, String>()

  init {
    Fetch.setDefaultInstanceConfiguration(
      FetchConfiguration.Builder(context)
        .setDownloadConcurrentLimit(CONCURRENT_DOWNLOAD_LIMIT)
        .setAutoRetryMaxAttempts(RETRY_TIMES)
        .setProgressReportingInterval(1000L)
        .enableHashCheck(true)
        .setHttpDownloader(
          object : OkHttpDownloader(
            okHttpClient,
            fileDownloaderType = Downloader.FileDownloaderType.PARALLEL
          ) {
            override fun verifyContentHash(
              request: Downloader.ServerRequest,
              hash: String
            ): Boolean {
              return request.fileUri.toFile().checkMd5(contentHashes[request.id] ?: hash)
            }

            override fun getHeadRequestMethodSupported(request: Downloader.ServerRequest): Boolean {
              return true
            }
          })
        .build()
    )

    fetch = Fetch.getDefaultInstance()
  }

  @Suppress("OPT_IN_USAGE")
  override fun download(
    packageName: String,
    installPackageInfo: InstallPackageInfo,
  ): Flow<DownloadInfo> = installPackageInfo.run {
    flow<Unit> {
      if (installPackageInfo.hasObb()) {
        installPermissions.checkIfCanWriteExternal()
      }
      installPermissions.checkIfCanInstall()
      emit(Unit)
    }.flatMapLatest {
      downloadFiles(packageName, versionCode, installationFiles)
    }.map { (progress, downloadedBytes) ->
      coroutineContext.ensureActive()
      DownloadInfo(
        progress = (progress * 100).toInt(),
        downloadedBytes = downloadedBytes
      )
    }
  }
    .also { InstallerWorker.enqueue(context) }

  private fun downloadFiles(
    packageName: String,
    versionCode: Long,
    installationFiles: Set<InstallationFile>
  ): Flow<Pair<Double, Long>> {
    val totalSize = installationFiles.sumOf { it.fileSize }.toDouble()

    val destinationDir = File(downloadsPath, packageName).apply {
      if (!exists()) {
        mkdirs().let {
          if (!it) throw IllegalStateException("Can't create download folder: $downloadsPath/$packageName")
        }
      }
    }

    val requests = installationFiles.mapNotNull { file ->
      File(destinationDir, file.name)
        .takeUnless { it.checkMd5(file.md5) }
        ?.let { localFile ->
          Request(file.url, localFile.toUri())
            .apply {
              priority = Priority.NORMAL
              networkType = NetworkType.ALL
              groupId = packageName.asGroupId()
              addHeader(VERSION_CODE, versionCode.toString())
              addHeader(PACKAGE, packageName)
              addHeader(FILE_TYPE, file.type.toString())
            }
            .also {
              contentHashes.put(it.id, file.md5)
            }
        }
    }

    val initialBytes = mutableMapOf<Int, Long>()
    val downloadedBytes = mutableMapOf<Int, Long>()
    val totalDownloadedBytes = mutableMapOf<Int, Long>()

    var listener: FetchFileDownloadListener? = null

    return callbackFlow {
      trySend(0.0 to 0L)

      if (requests.isEmpty()) {
        trySend(1.0 to 0L)
        close()
      }

      listener = object : FetchFileDownloadListener {
        override fun onAdded(download: Download) {
          initialBytes.put(download.id, 0L)
          downloadedBytes.put(download.id, 0L)
          totalDownloadedBytes.put(download.id, 0L)
          super.onAdded(download)
        }

        override fun onStarted(
          download: Download,
          downloadBlocks: List<DownloadBlock>,
          totalBlocks: Int
        ) {
          initialBytes.put(download.id, download.downloaded)
          super.onStarted(download, downloadBlocks, totalBlocks)
        }

        override fun onCompleted(download: Download) {
          fetch.hasActiveDownloads(false) {
            if (!it) {
              trySend(1.0 to downloadedBytes.values.sum())
              close()
            }
          }
        }

        override fun onError(
          download: Download,
          error: Error,
          throwable: Throwable?
        ) {
          close(throwable ?: AbortException("Error downloading files"))
        }

        override fun onProgress(
          download: Download,
          etaInMilliSeconds: Long,
          downloadedBytesPerSecond: Long
        ) {
          totalDownloadedBytes.replace(download.id, download.downloaded)
          initialBytes[download.id]?.let { initialBytes ->
            downloadedBytes.replace(download.id, download.downloaded - initialBytes)
          }
          trySend((totalDownloadedBytes.values.sum() / totalSize) to downloadedBytes.values.sum())
        }
      }

      fetch.addListener(listener)

      fetch.enqueue(requests) { enqueuedRequests ->
        enqueuedRequests.find { it.second != Error.NONE }?.let {
          close(it.second.throwable ?: IllegalStateException("Error enqueuing requests"))
        }
      }

      awaitClose {}
    }
      .distinctUntilChanged()
      .onCompletion {
        listener?.let { fetch.removeListener(it) }
        contentHashes.clear()

        if (it == null) {
          //Removes temporary files and cache from Fetch. Does not delete the downloaded files
          fetch.removeGroup(packageName.asGroupId())
        } else if (it is CancellationException) {
          fetch.cancelGroup(packageName.asGroupId())
        }
      }
  }

  private fun String.asGroupId(): Int = hashCode()
}
