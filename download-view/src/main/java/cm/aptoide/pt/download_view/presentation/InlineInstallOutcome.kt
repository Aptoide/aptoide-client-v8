package cm.aptoide.pt.download_view.presentation

import cm.aptoide.pt.feature_apps.data.App

/** What an install does once no external install could be started for it. */
internal enum class InlineInstallOutcome {
  /** The regular download and install path, as for any app without an external install. */
  REGULAR_INSTALL,

  /** An error the user can retry: the app may only install externally, and that failed. */
  ERROR,

  /** Canceled, for an app that may only install externally and whose stages were all shown. */
  CANCELED,
}

/**
 * Decided from the resolver's rules for [app] and whether every external stage already ran
 * ([ladderExhausted]) or none could even start.
 */
internal fun inlineInstallOutcome(
  resolver: InlineInstallResolver?,
  app: App,
  ladderExhausted: Boolean,
): InlineInstallOutcome = when {
  resolver == null -> InlineInstallOutcome.REGULAR_INSTALL
  ladderExhausted && !resolver.allowsRegularFallback(app) -> InlineInstallOutcome.CANCELED
  resolver.requiresInlineInstall(app) -> InlineInstallOutcome.ERROR
  else -> InlineInstallOutcome.REGULAR_INSTALL
}
