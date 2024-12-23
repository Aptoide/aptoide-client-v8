package cm.aptoide.pt.installer

import androidx.annotation.Keep

/**
 * An exception that wraps exceptions caused by a file download failure.
 *
 * @property url - the file download url that caused the exception.
 * @property cause - the exception thrown by the file download failure.
 */
@Keep
class DownloadException(
  val url: String,
  override val cause: Throwable
) : Exception()
