package cm.aptoide.pt.feature_apps.domain

interface AppSource {
  val appId: Long?
  val packageName: String?

  fun asSource(): String = appId?.takeIf { it > 0 }?.let { "app_id=$it" }
    ?: packageName?.let { "package_name=$it" }
    ?: ""

  companion object {
    fun of(appId: Long?, packageName: String?) = object : AppSource {
      override val appId = appId
      override val packageName = packageName
    }
  }
}
