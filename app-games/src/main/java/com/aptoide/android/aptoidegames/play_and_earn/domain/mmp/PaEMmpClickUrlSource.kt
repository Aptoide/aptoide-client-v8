package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import javax.inject.Inject

/** A developer's MMP click link and the P&E campaign it belongs to. */
data class PaEMmpClickTemplate(
  val url: String,
  val campaignId: String,
)

/** Where the MMP click link of a Play & Earn game comes from. */
interface PaEMmpClickUrlSource {
  suspend fun clickTemplate(packageName: String): PaEMmpClickTemplate?
}

/**
 * The click link of the game's serving campaign, as `/missions` returns it. Asked fresh from the
 * network: the cached missions don't keep the link.
 */
class MissionsPaEMmpClickUrlSource @Inject constructor(
  private val missionsRepository: PaEMissionsRepository,
) : PaEMmpClickUrlSource {

  override suspend fun clickTemplate(packageName: String): PaEMmpClickTemplate? {
    val missions = missionsRepository.getCampaignMissions(packageName, forceRefresh = true)
      .getOrNull() ?: return null
    return PaEMmpClickTemplate(
      url = missions.mmpClickUrl ?: return null,
      campaignId = missions.campaignId ?: return null,
    )
  }
}
