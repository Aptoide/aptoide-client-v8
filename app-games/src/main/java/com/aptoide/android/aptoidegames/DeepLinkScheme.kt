package com.aptoide.android.aptoidegames

/**
 * Whether [scheme] is this brand's deep-link scheme: `ag` on Aptoide Games, `aptoide` on Vanilla
 * (the scheme part of [BuildConfig.DEEP_LINK_SCHEMA]). The app's own notifications build their deep
 * links with that schema, so MainActivity follows exactly this scheme and nothing else: `promocode`
 * has its own handling and http(s) is handled as an app link.
 *
 * Kept free of `android.net.Uri` so it runs in plain JVM unit tests.
 */
fun isBrandDeepLinkScheme(
  scheme: String?,
  deepLinkSchema: String = BuildConfig.DEEP_LINK_SCHEMA,
): Boolean = scheme == deepLinkSchema.substringBefore("://")
