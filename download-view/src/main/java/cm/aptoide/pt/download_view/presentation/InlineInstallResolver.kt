package cm.aptoide.pt.download_view.presentation

import android.content.Intent
import cm.aptoide.pt.feature_apps.data.App

/**
 * Decides whether installing [App] should be diverted to an external install flow
 * (e.g. Google Play inline installs on the Play-distributed build) instead of the
 * regular InstallManager download+install path.
 *
 * The binding is optional (see [cm.aptoide.pt.download_view.di.InlineInstallResolverModule]):
 * apps that never divert installs don't bind an implementation.
 */
interface InlineInstallResolver {

  /**
   * Returns a ready-to-launch [Intent] to install [app] externally,
   * or null to proceed with the regular install path.
   */
  suspend fun resolveInlineInstall(app: App): Intent?

  /**
   * Returns a ready-to-launch fallback [Intent] for when the [resolveInlineInstall] launch
   * was rejected without any UI being shown, or null when no fallback stage exists.
   */
  suspend fun resolveFallbackInstall(app: App): Intent? = null

  /** Called when the external install UI was launched for [app]. */
  fun onInlineInstallStarted(app: App) {}

  /** Called when the external install UI was visibly dismissed without [app] being installed. */
  fun onInlineInstallCanceled(app: App) {}

  /**
   * Called when every external install stage for [app] was rejected without showing any UI,
   * right before the install falls through to the regular install path.
   */
  fun onInlineInstallUnavailable(app: App) {}

  /**
   * Whether [app] may fall through to the regular install path when every external
   * install stage is exhausted. When false the install ends as canceled instead -
   * for apps that must only ever be installed externally.
   */
  fun allowsRegularFallback(app: App): Boolean = true

  /**
   * Returns true while an external install for [packageName] is still ongoing,
   * so the UI state can be restored after the view model is recreated.
   */
  fun isInlineInstallOngoing(packageName: String): Boolean = false
}
