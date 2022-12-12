package cm.aptoide.pt.feature_home.domain

data class BundleAction(
  val widgetActionTypeName: WidgetActionEventName,
  val mainBundleIdentifier: String
)