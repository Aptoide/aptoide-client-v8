package cm.aptoide.pt.installer.network

import cm.aptoide.pt.aptoide_network.di.DownloadsOKHttp
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
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.asResponseBody
import okio.Buffer
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class DownloaderRepository @Inject constructor(
  @DownloadsPath private val downloadsPath: File,
  @DownloadsOKHttp private val okHttpClient: OkHttpClient
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
    val destinationDir = File(downloadsPath, packageName).apply {
      if (!exists()) {
        mkdirs().let {
          if (!it) throw IllegalStateException("Can't create download folder: $downloadsPath/$packageName")
        }
      }
    }

    val destinationFile = File(destinationDir, installationFile.name)

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
        val body = response
          .takeIf { it.isSuccessful }
          ?.body
          ?: throw HttpException(
            Response.error<Any>(
              response.body?.toBuffered() ?: throw IOException("No body present"),
              response
            )
          )
        val totalBytes = partialFileSize + body.contentLength()
        body.byteStream().use { inputStream ->
          destinationFile.createNewFile()
          FileOutputStream(destinationFile, partialFileSize > 0).use { outputStream ->
            inputStream.copyWithProgressTo(outputStream).collect { bytesCopied ->
              emit(((partialFileSize + bytesCopied) * 0.98) / totalBytes)
            }
          }
        }
        if (!destinationFile.checkMd5(installationFile.md5)) {
          destinationFile.delete()
          throw IOException("MD5 check failed")
        }
        emit(1.0)
      }
    }
      .distinctUntilChanged()
      .retry(retries = RETRY_TIMES)
      .onCompletion { it?.printStackTrace() }
  }
}

// Copied from the Retrofit's package private Utils and turned into an extension function.
@Throws(IOException::class) fun ResponseBody.toBuffered(): ResponseBody {
  val buffer = Buffer()
  source().readAll(buffer)
  return buffer.asResponseBody(contentType(), contentLength())
}
