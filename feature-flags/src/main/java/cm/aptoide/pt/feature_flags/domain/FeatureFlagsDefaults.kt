package cm.aptoide.pt.feature_flags.domain

import org.json.JSONObject

/**
 * The flag values an app ships with. They apply under whatever the remote source or the last
 * saved snapshot provides, so a key neither of them carries still resolves to its default.
 * Binding one is optional: an app without it gets the remote values alone.
 */
fun interface FeatureFlagsDefaults {
  fun get(): JSONObject
}
