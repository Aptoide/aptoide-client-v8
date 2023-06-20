package cm.aptoide.pt.aptoide_network.q

import android.app.ActivityManager
import android.app.UiModeManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Build.VERSION
import android.util.Base64

class QManager(
  private val resources: Resources,
  private val activityManager: ActivityManager,
  private val uiModeManager: UiModeManager
) {
  private var minSdk: Int? = null
  private var cpuAbi: String? = null
  private var screenSize: String? = null
  private var glEs: String? = null
  private var densityDpi: Int? = null
  private var cachedFilters: String? = null

  fun getFilters(hwSpecsFilter: Boolean): String? {
    if (!hwSpecsFilter) return null
    cachedFilters ?: run { cachedFilters = computeFilters() }
    return cachedFilters
  }

  private fun computeFilters(): String {
    val filters = "maxSdk=${getMinSdk()}" +
      "&maxScreen=${getScreenSize()}" +
      "&maxGles=${getGlesVer()}" +
      "&myCPU=${getCpuAbi()}" +
      "&leanback=${hasLeanback()}" +
      "&myDensity=${getDensityDpi()}"

    return Base64.encodeToString(filters.toByteArray(), 0)
      .replace("=", "")
      .replace("/", "*")
      .replace("+", "_")
      .replace("\n", "")
  }

  private fun getMinSdk(): Int? {
    minSdk ?: run { minSdk = VERSION.SDK_INT }
    return minSdk
  }

  private fun getScreenSize(): String? {
    screenSize ?: run { screenSize = computeScreenSize() }
    return screenSize
  }

  private fun computeScreenSize(): String =
    when (getScreenSizeInt()) {
      Configuration.SCREENLAYOUT_SIZE_UNDEFINED -> "undefined"
      Configuration.SCREENLAYOUT_SIZE_SMALL -> "small"
      Configuration.SCREENLAYOUT_SIZE_NORMAL -> "normal"
      Configuration.SCREENLAYOUT_SIZE_LARGE -> "large"
      Configuration.SCREENLAYOUT_SIZE_XLARGE -> "xlarge"
      else -> "unknown"
    }

  private fun getScreenSizeInt(): Int =
    resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

  private fun getGlesVer(): String? {
    glEs ?: run { glEs = activityManager.deviceConfigurationInfo.glEsVersion }
    return glEs
  }

  private fun getCpuAbi(): String? {
    cpuAbi ?: run { cpuAbi = Build.SUPPORTED_ABIS.joinToString { "," } }
    return cpuAbi
  }

  private fun hasLeanback(): String {
    return if (uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION)
      "1" else "0"
  }

  private fun getDensityDpi(): Int? {
    densityDpi ?: run { densityDpi = computeDensityDpi() }
    return densityDpi
  }

  private fun computeDensityDpi(): Int = resources.displayMetrics.densityDpi
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
}
