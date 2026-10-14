package com.aptoide.android.aptoidegames.play_and_earn.domain.mmp

import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.test.gherkin.coScenario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

internal class MissionsPaEMmpClickUrlSourceTest {

  private class FakeMissionsRepository(private val result: Result<PaEMissions>) :
    PaEMissionsRepository {
    val requests = mutableListOf<Pair<String, Boolean>>()

    override suspend fun getCampaignMissions(packageName: String, forceRefresh: Boolean) =
      result.also { requests += packageName to forceRefresh }

    override suspend fun getEventMissions(): Result<List<PaEMission>> = Result.success(emptyList())
    override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> =
      emptyFlow()

    override suspend fun getCachedMissions(packageName: String): PaEMissions? = null
    override suspend fun markMissionAsCompleted(packageName: String, missionTitle: String) = Unit
  }

  private fun missions(campaignId: String?, url: String?) = PaEMissions(
    checkpoints = emptyList(),
    missions = emptyList(),
    campaignId = campaignId,
    mmpClickUrl = url,
  )

  private val url = "https://app.appsflyer.com/com.game?pid=aptoide_int"

  @Test
  fun `Uses the click link the missions carry`() = coScenario {
    m Given "missions for the game with a click link"
    val repository = FakeMissionsRepository(Result.success(missions("7", url)))

    m When "the click template is asked"
    val template = MissionsPaEMmpClickUrlSource(repository).clickTemplate("com.game")

    m Then "it is the link and campaign of the missions, fetched fresh from the network"
    assertEquals(PaEMmpClickTemplate(url = url, campaignId = "7"), template)
    assertEquals(listOf("com.game" to true), repository.requests)
  }

  @Test
  fun `No template when the campaign has no click link`() = coScenario {
    m Given "missions without a click link"
    val repository = FakeMissionsRepository(Result.success(missions("7", null)))

    m When "the click template is asked"
    val template = MissionsPaEMmpClickUrlSource(repository).clickTemplate("com.game")

    m Then "there is none"
    assertNull(template)
  }

  @Test
  fun `No template without a campaign id`() = coScenario {
    m Given "missions with a link but no campaign"
    val repository = FakeMissionsRepository(Result.success(missions(null, url)))

    m When "the click template is asked"
    val template = MissionsPaEMmpClickUrlSource(repository).clickTemplate("com.game")

    m Then "there is none"
    assertNull(template)
  }

  @Test
  fun `No template when the missions can't be loaded`() = coScenario {
    m Given "the missions request fails"
    val repository = FakeMissionsRepository(Result.failure(RuntimeException("offline")))

    m When "the click template is asked"
    val template = MissionsPaEMmpClickUrlSource(repository).clickTemplate("com.game")

    m Then "there is none"
    assertNull(template)
  }
}
