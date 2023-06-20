package cm.aptoide.pt.environment_info.repository

import android.app.ActivityManager
import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject

class DeviceInfoRepository @Inject constructor(@ApplicationContext private val context: Context) {

  private val uiModeManager: UiModeManager =
    context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

  val architecture: String?
    get() = System.getProperty("os.arch")

  val screenWidth: Int get() = context.resources.displayMetrics.widthPixels

  val screenHeight: Int get() = context.resources.displayMetrics.heightPixels

  val density: Int
    get() = context.resources.displayMetrics.densityDpi

  val glEsVersion: String
    get() = (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
      .deviceConfigurationInfo.glEsVersion

  val screenSizeInt: Int
    get() = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

  val currentModeType: Int get() = uiModeManager.currentModeType

  fun getLogsFile(header: String): Uri {
    try {
      val directory = File(context.cacheDir, "logs")
      directory.mkdirs()

      val file = File(directory, "logs.txt")
      val authority = context.packageName + ".fileprovider"

      file.bufferedWriter().use { out -> out.write(saveLog(header)) }
      return FileProvider.getUriForFile(context, authority, file)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return Uri.parse("")
  }

  // 1) "logcat -d" -> Default Logcat behaviour
  // 2) "logcat -d *:V" -> Verbose
  // 3) "logcat -d *:D" -> Debug
  // 4) "logcat -d *:I" -> Info
  // 5) "logcat -d *:W" -> Warn
  // 6) "logcat -d *:E" -> Error
  private fun saveLog(logsDeviceInfo: String): String {
    val stringBuilderLog = StringBuilder().append(logsDeviceInfo)
    val process = Runtime.getRuntime().exec("logcat -d *:D") // Command
    val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
    var line: String?
    while (bufferedReader.readLine().also { line = it } != null) {
      stringBuilderLog.append(line).append("\n")
    }
    return stringBuilderLog.toString()
  }
}
