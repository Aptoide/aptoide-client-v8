package cm.aptoide.pt.feature_apps.domain

import java.util.Locale

interface AppSource {
  val appId: Long? get() = null
  val packageName: String? get() = null

  fun asSource(): String = appId?.takeIf { it > 0 }?.let { "app_id=$it" }
    ?: packageName?.let { "package_name=$it" }
    ?: ""

  companion object {
    fun of(appId: Long?, packageName: String?) = object : AppSource {
      override val appId = appId
      override val packageName = packageName
    }

    fun String.appendIfRequired(storeName: String) =
      if (contains("package_name=") && !contains("com.appcoins.wallet")) {
        "$this/store_name=${storeName.lowercase(Locale.ROOT)}"
      } else this
  }
}
