package com.aptoide.android.aptoidegames.installer

/**
 * Directly distributed builds never inline install - no [PlayCatalogChecker] is bound - so
 * there is nothing to attribute and the feed keeps its one-tap install.
 */
internal const val FEED_INSTALL_DIVERTS_TO_APPVIEW = false
