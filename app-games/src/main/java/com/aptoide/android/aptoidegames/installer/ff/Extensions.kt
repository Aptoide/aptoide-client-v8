package com.aptoide.android.aptoidegames.installer.ff

import cm.aptoide.pt.feature_flags.domain.FeatureFlags

suspend fun FeatureFlags.isFetchDownloaderEnabled(): Boolean? = getFlag("use_fetch_downloader")
