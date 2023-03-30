package cm.aptoide.pt.installer.platform

import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.io.OutputStream


/**
 * Copies this stream to the given output stream, returning the number of bytes copied progressively.
 *
 * **Note** It is the caller's responsibility to close both of these resources.
 */
@Suppress("BlockingMethodInNonBlockingContext")
fun InputStream.copyWithProgressTo(
  outputStream: OutputStream,
  bufferSize: Int = DEFAULT_BUFFER_SIZE,
) = flow {
  val buffer = ByteArray(bufferSize)
  var bytesCopied = 0L
  var bytes = read(buffer)
  while (bytes >= 0) {
    outputStream.write(buffer, 0, bytes)
    bytesCopied += bytes
    bytes = read(buffer)
    emit(bytesCopied)
  }
}
