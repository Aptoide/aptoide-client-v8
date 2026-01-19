package com.aptoide.android.aptoidegames.feature_rtb.repository

import cm.aptoide.pt.feature_campaigns.CampaignImpl
import com.aptoide.android.aptoidegames.feature_rtb.data.RTBApp

interface RTBRepository {
  suspend fun getRTBApps(placement: String): List<RTBApp>

  fun getCachedCampaigns(packageName: String): CampaignImpl?
}
