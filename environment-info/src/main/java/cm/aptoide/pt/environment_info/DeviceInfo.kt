package cm.aptoide.pt.environment_info

import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import cm.aptoide.pt.environment_info.repository.DeviceInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceInfo @Inject constructor(
  private val deviceInfoRepository: DeviceInfoRepository
) {

  fun getLogs(): Uri = deviceInfoRepository.getLogsFile(
    "Android Build Version: ${getApiLevel()}\n" +
      "Build Model: ${getModel()}\n" +
      "Device: ${Build.DEVICE}\n" +
      "Brand: ${Build.BRAND}\n" +
      "CPU: ${getSupportedABIs()}\n" +
      "\nLogs: \n"
  )

  fun getDeviceInfoSummary(): String = "SDK version: ${getSdk()}\n" +
    "Screen size: ${getScreenSize()}\n" +
    "ESGL version: ${deviceInfoRepository.glEsVersion}\n" +
    "Screen code: ${getNumericScreenSize()}/${getDensityDpi()}\n" +
    "CPU: ${getSupportedABIs()}\n" +
    "Density: ${getDensityDpi()} ${getDensityName()}"

  fun getApiLevel(): Int = Build.VERSION.SDK_INT

  fun getModel(): String = Build.MODEL

  fun getProductCode(): String = Build.PRODUCT

  fun getDensityDpi(): Int = deviceInfoRepository.density
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

  fun getDensityName(): String = deviceInfoRepository.density
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

  fun getScreenSize(): String =
    when (deviceInfoRepository.screenSizeInt) {
      Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> "undefined"
      Configuration.SCREENLAYOUT_SIZE_SMALL -> "small"
      Configuration.SCREENLAYOUT_SIZE_NORMAL -> "normal"
      Configuration.SCREENLAYOUT_SIZE_LARGE -> "large"
      Configuration.SCREENLAYOUT_SIZE_XLARGE -> "xlarge"
      else -> "unknown"
    }

  fun getSdk() = Build.VERSION.SDK_INT

  fun getAndroidRelease(): String = Build.VERSION.RELEASE

  fun getNumericScreenSize(): Int {
    return (deviceInfoRepository.screenSizeInt + 1) * 100
  }

  fun getArchitecture() = deviceInfoRepository.architecture

  fun getSupportedABIs() = Build.SUPPORTED_ABIS.joinToString()

  fun getScreenDimensions() =
    "${deviceInfoRepository.screenWidth}x${deviceInfoRepository.screenHeight}"

  fun getManufacturer(): String = Build.MANUFACTURER

  fun getGlEsVersion() = deviceInfoRepository.glEsVersion

  fun hasLeanback(): String {
    return if (deviceInfoRepository.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION)
      "1" else "0"
  }
}
