package cm.aptoide.pt.network.model

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import cm.aptoide.pt.network.data.PreferencesPersister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AptoideMd5Manager @Inject constructor(
  private val preferencesPersister: PreferencesPersister,
  private val packageManager: PackageManager?,
  private val packageName: String?,
) {

  private var cachedMd5: String
    get() = runBlocking {
      withContext(Dispatchers.Default) {
        preferencesPersister.get().first()
      }
    }
    set(md5) = runBlocking {
      withContext(Dispatchers.Default) {
        preferencesPersister.set(md5)
      }
    }

  fun getAptoideMd5(): String = cachedMd5

  fun calculateMd5Sum() {
    // Look for stored md5
    if (cachedMd5.isNotEmpty()) return

    // Calculate md5
    try {
      cachedMd5 =
        computeMd5(
          packageManager!!.getPackageInfo(packageName!!, 0)
        ) ?: ""
    } catch (e: NameNotFoundException) {
      e.printStackTrace()
    }
  }

  private fun computeMd5(packageInfo: PackageInfo): String? {
    val sourceDir = packageInfo.applicationInfo.sourceDir
    val apkFile = File(sourceDir)
    val time = System.currentTimeMillis()
    val buffer = ByteArray(1024 * 1024)
    var read: Int
    var i: Int
    var md5hash: String
    try {
      val digest = MessageDigest.getInstance("MD5")
      val inputS: InputStream = FileInputStream(apkFile)
      while (inputS.read(buffer).also { read = it } > 0) {
        digest.update(buffer, 0, read)
      }
      val md5sum = digest.digest()
      val bigInt = BigInteger(1, md5sum)
      md5hash = bigInt.toString(16)
      inputS.close()
    } catch (e: Exception) {
      e.printStackTrace()
      return null
    }
    if (md5hash.length != 33) {
      var tmp = ""
      i = 1
      while (i < 33 - md5hash.length) {
        tmp += "0"
        i++
      }
      md5hash = tmp + md5hash
    }
    Timber.v("computeMd5: duration: ${System.currentTimeMillis() - time} ms")
    return md5hash
  }
}
