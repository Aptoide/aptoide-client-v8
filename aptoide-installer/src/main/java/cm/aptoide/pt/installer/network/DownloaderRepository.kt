package cm.aptoide.pt.installer.network

import cm.aptoide.pt.aptoide_network.di.SimpleOkHttp
import cm.aptoide.pt.extensions.checkMd5
import cm.aptoide.pt.install_manager.dto.InstallationFile
import cm.aptoide.pt.installer.di.DownloadsPath
import cm.aptoide.pt.installer.platform.copyWithProgressTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.retry
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DownloaderRepository @Inject constructor(
  @DownloadsPath private val downloadsPath: File,
  @SimpleOkHttp private val okHttpClient: OkHttpClient
) {

  companion object {
    const val RETRY_TIMES = 3L
    const val VERSION_CODE = "versioncode"
    const val PACKAGE = "package"
    const val FILE_TYPE = "fileType"
    const val RANGE = "Range"
  }

  fun download(
    packageName: String,
    versionCode: Long,
    installationFile: InstallationFile
  ): Flow<Double> {
    val destinationFile = File(downloadsPath, installationFile.name)
    return flow {
      emit(0.0)

      if (destinationFile.checkMd5(installationFile.md5)) {
        emit(1.0)
        return@flow
      }

      val partialFileSize = if (destinationFile.exists()) destinationFile.length() else 0

      val request: Request = Request.Builder()
        .url(installationFile.url)
        .addHeader(VERSION_CODE, versionCode.toString())
        .addHeader(PACKAGE, packageName)
        .addHeader(FILE_TYPE, installationFile.type.toString())
        .addHeader(RANGE, "bytes=${partialFileSize}-")
        .build()

      okHttpClient.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        response.body?.run {
          byteStream().use { inputStream ->
            destinationFile.createNewFile()
            FileOutputStream(destinationFile, partialFileSize > 0).use { outputStream ->
              val totalBytes = partialFileSize + contentLength()
              inputStream.copyWithProgressTo(outputStream).collect { bytesCopied ->
                emit(((partialFileSize + bytesCopied) * 0.98) / totalBytes)
              }
            }
          }
          if (destinationFile.checkMd5(installationFile.md5)) {
            emit(1.0)
            return@flow
          }
          destinationFile.delete()
          throw IOException("MD5 check failed")
        } ?: throw IOException("No body present")
      }
    }
      .distinctUntilChanged()
      .retry(retries = RETRY_TIMES)
      .onCompletion { it?.printStackTrace() }
  }
}
