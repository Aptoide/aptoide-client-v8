package cm.aptoide.pt.extensions

import android.annotation.SuppressLint

fun isMIUI(): Boolean = !getSystemProperty("ro.miui.ui.version.name").isNullOrBlank()

@SuppressLint("PrivateApi")
fun isMiuiOptimizationDisabled(): Boolean =
  if ("0" == getSystemProperty("persist.sys.miui_optimization")) {
    true
  } else try {
    Class.forName("android.miui.AppOpsUtils")
      .getDeclaredMethod("isXOptMode")
      .invoke(null) as Boolean
  } catch (_: java.lang.Exception) {
    false
  }

@SuppressLint("PrivateApi")
private fun getSystemProperty(key: String): String? {
  return try {
    Class.forName("android.os.SystemProperties")
      .getDeclaredMethod("get", String::class.java)
      .invoke(null, key) as String
  } catch (_: Exception) {
    null
  }
}
