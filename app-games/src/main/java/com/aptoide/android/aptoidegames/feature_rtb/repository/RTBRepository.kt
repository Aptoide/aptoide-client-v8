package com.aptoide.android.aptoidegames.feature_rtb.repository

import cm.aptoide.pt.feature_apps.data.App

interface RTBRepository {
  suspend fun getRTBApps(placement: String): List<App>
}
