package cm.aptoide.pt.installer.platform

import android.text.TextUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


fun File.checkMd5(md5: String): Boolean = TextUtils.equals(calculateMD5(), md5)

fun File.calculateMD5(): String? = try {
  MessageDigest.getInstance("MD5")
} catch (e: NoSuchAlgorithmException) {
  null
}?.let { digest ->
  try {
    FileInputStream(this).use { stream ->
      val buffer = ByteArray(8192)
      var read: Int
      return try {
        while (stream.read(buffer).also { read = it } > 0) {
          digest.update(buffer, 0, read)
        }
        val md5sum = digest.digest()
        val bigInt = BigInteger(1, md5sum)
        var output = bigInt.toString(16)
        // Fill to 32 chars
        output = String.format("%32s", output).replace(' ', '0')
        output
      } catch (e: IOException) {
        throw RuntimeException("Unable to process file for MD5", e)
      }
    }
  } catch (e: FileNotFoundException) {
    null
  }
}
