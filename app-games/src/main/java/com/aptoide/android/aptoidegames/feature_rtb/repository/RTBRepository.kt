package com.aptoide.android.aptoidegames.feature_rtb.repository

import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp

interface RTBRepository {
  suspend fun getRTBApps(placement: String): List<RTBApp>
}
