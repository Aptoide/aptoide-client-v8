package com.aptoide.android.aptoidegames.installer.ff

import cm.aptoide.pt.feature_flags.domain.FeatureFlags

suspend fun FeatureFlags.getDownloaderVariant(): String? = getFlagAsString("downloader_variant")
