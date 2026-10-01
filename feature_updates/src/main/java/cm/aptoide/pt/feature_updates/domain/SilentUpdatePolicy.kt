package cm.aptoide.pt.feature_updates.domain

import cm.aptoide.pt.feature_apps.data.App

/**
 * Which updates may be installed without the user asking - by the auto-update and VIP
 * workers. Bound only by builds that cannot install every app themselves; the others
 * install any update silently, as they always did.
 */
fun interface SilentUpdatePolicy {
  fun allows(app: App): Boolean
}
