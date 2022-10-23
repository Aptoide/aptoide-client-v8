package cm.aptoide.pt.feature_apps.domain

data class BundleAction(
  val widgetActionTypeName: WidgetActionEventName,
  val mainBundleIdentifier: String
)