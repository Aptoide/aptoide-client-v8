package cm.aptoide.pt.settings.data

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import cm.aptoide.pt.settings.domain.DeviceInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import javax.inject.Inject

class DeviceInfoRepository @Inject constructor(@ApplicationContext private val context: Context) {

  fun getDeviceInfo(): DeviceInfo = DeviceInfo(
    sdkVersion = Build.VERSION.SDK_INT,
    screenSize = getScreenSize(),
    esglVersion = getGlEsVersion(),
    cpu = Build.SUPPORTED_ABIS.joinToString(),
    densityDPI = getDensityDpi(),
    densityName = getDensityName(),
  )

  fun createLogsFile(): Uri {
    val logsDeviceInfo = "Android Build Version: ${Build.VERSION.SDK_INT}\n" +
      "Build Model: ${Build.MODEL}\n" +
      "Device: ${Build.DEVICE}\n" +
      "Brand: ${Build.BRAND}\n" +
      "CPU: ${Build.SUPPORTED_ABIS.joinToString()}\n" +
      "\nLogs: \n"
    try {
      val directory = File(context.cacheDir, "logs")
      directory.mkdirs()

      val file = File(directory, "logs.txt")
      val authority = context.packageName + ".fileprovider"

      file.bufferedWriter().use { out -> out.write(saveLog(logsDeviceInfo)) }
      return FileProvider.getUriForFile(context, authority, file)
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return Uri.parse("")
  }

  private fun getDensityDpi(): Int = context.resources.displayMetrics.densityDpi
    .let { dpi ->
      when {
        dpi <= 120 -> 120
        dpi <= 160 -> 160
        dpi <= 213 -> 213
        dpi <= 240 -> 240
        dpi <= 320 -> 320
        dpi <= 480 -> 480
        else -> 640
      }
    }

  private fun getDensityName(): String = context.resources.displayMetrics.densityDpi
    .let { dpi ->
      when {
        dpi <= 120 -> " ldpi"
        dpi <= 160 -> " mdpi"
        dpi <= 213 -> " tvdpi"
        dpi <= 240 -> " hdpi"
        dpi <= 320 -> " xhdpi"
        dpi <= 480 -> " xxhdpi"
        else -> " xxxhdpi"
      }
    }

  private fun getScreenSize(): String =
    when (getScreenSizeInt()) {
      Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> "undefined"
      Configuration.SCREENLAYOUT_SIZE_SMALL -> "small"
      Configuration.SCREENLAYOUT_SIZE_NORMAL -> "normal"
      Configuration.SCREENLAYOUT_SIZE_LARGE -> "large"
      Configuration.SCREENLAYOUT_SIZE_XLARGE -> "xlarge"
      else -> "unknown"
    }

  private fun getGlEsVersion(): String =
    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
      .deviceConfigurationInfo.glEsVersion

  private fun getScreenSizeInt(): Int =
    context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

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
