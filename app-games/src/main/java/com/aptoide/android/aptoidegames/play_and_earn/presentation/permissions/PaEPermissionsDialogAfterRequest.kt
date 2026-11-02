package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

/** The dialog the Start Earning screen shows once the user is back from the permission screens. */
enum class PaEPermissionsDialogKind {
  /** Both permissions granted: nothing to say, the screen closes itself on resume. */
  None,

  /** "Permissions locked?": reveals the way to Android's restricted settings. */
  Locked,

  /** "Rewards are paused": the user declined, offer to ask again. */
  Denied,
}

/**
 * Picks the dialog for the permissions still missing after a request (AND-922).
 *
 * On Android 15+ a sideloaded app's usage-access and overlay toggles are greyed out until the user
 * allows restricted settings, and the app cannot tell a locked toggle from a declined one. So any
 * missing permission there shows [PaEPermissionsDialogKind.Locked], every time, since the card it
 * reveals is the only way to unlock the toggles (tech lead decision, 2026-10-30). Before Android 15
 * there is nothing to unlock, so a missing permission is a refusal.
 */
fun paePermissionsDialogAfterRequest(
  requiresRestrictedSettings: Boolean,
  hasOverlay: Boolean,
  hasUsageStats: Boolean,
): PaEPermissionsDialogKind = when {
  hasOverlay && hasUsageStats -> PaEPermissionsDialogKind.None
  requiresRestrictedSettings -> PaEPermissionsDialogKind.Locked
  else -> PaEPermissionsDialogKind.Denied
}
