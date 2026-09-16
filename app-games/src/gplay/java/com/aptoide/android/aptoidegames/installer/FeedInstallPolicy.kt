package com.aptoide.android.aptoidegames.installer

/**
 * Play-distributed builds can divert an install into a Google Play inline install, which must
 * carry the "Google Play" attribution before the user taps. Feed cards cannot label cheaply -
 * it would cost one catalog lookup per visible card - so they route to AppView instead, which
 * prefetches the catalog status and labels its own button.
 */
internal const val FEED_INSTALL_DIVERTS_TO_APPVIEW = true
