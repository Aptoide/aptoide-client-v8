package cm.aptoide.pt.feature_apps.domain

/**
 * The backend an [cm.aptoide.pt.feature_apps.data.App] was read from. The two describe
 * different things with the same fields - only a [V7] app carries the store flags and the
 * download location the Aptoide installer needs - so code that routes an install has to know
 * which one it is holding.
 */
enum class AppOrigin {
  V7,
  DEVICE_API,
}
