package com.aptoide.android.aptoidegames.newservices

import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_updates.domain.SilentUpdatePolicy
import com.aptoide.android.aptoidegames.installer.excludedFromPlayCatalog

/**
 * On the Play build only the apps that install through Aptoide may be updated without the
 * user asking; Play updates the rest, and this build has no download of its own for them.
 */
internal object PlaySilentUpdatePolicy : SilentUpdatePolicy {
  override fun allows(app: App): Boolean = app.excludedFromPlayCatalog()
}

/** Any update, as every other build installs them - the policy of the switch being off. */
internal object AnySilentUpdatePolicy : SilentUpdatePolicy {
  override fun allows(app: App): Boolean = true
}
