package com.aptoide.android.aptoidegames.home.analytics

import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta

val Bundle.meta get() = BundleMeta(tag, bundleSource.toString())
