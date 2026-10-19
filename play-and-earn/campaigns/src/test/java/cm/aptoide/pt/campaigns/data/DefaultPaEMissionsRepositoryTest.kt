package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.database.PaeMissionDao
import cm.aptoide.pt.campaigns.data.database.model.PaEMissionEntity
import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEEventMissionsJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionTypeJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import cm.aptoide.pt.campaigns.domain.PaEEvidenceSource
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.test.gherkin.coScenario
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-900/AND-901: the campaign's requirements and each mission's evidence source reach the
// domain from the network, and the requirements survive a cached read within the process.
internal class DefaultPaEMissionsRepositoryTest {

  private class FakeApi(var response: PaEMissionsJson) : PaECampaignsApi {
    var calls = 0
    override suspend fun getCampaigns(sections: List<String>): PaECampaignJson =
      throw UnsupportedOperationException()

    override suspend fun getCampaignMissions(packageName: String): PaEMissionsJson {
      calls++
      return response
    }

    override suspend fun getMissions(missionType: String): PaEEventMissionsJson =
      throw UnsupportedOperationException()
  }

  private class FakeDao : PaeMissionDao {
    val rows = mutableListOf<PaEMissionEntity>()
    override suspend fun getAppMissions(packageName: String) =
      rows.filter { it.packageName == packageName }
    override suspend fun insertAll(missions: List<PaEMissionEntity>) {
      rows += missions
    }

    override suspend fun clearAppMissions(packageName: String) {
      rows.removeAll { it.packageName == packageName }
    }

    override suspend fun updateMissionStatus(
      packageName: String,
      missionTitle: String,
      status: PaEMissionStatus,
    ) = Unit
  }

  private fun missionJson(title: String, evidenceSource: String?) = PaEMissionJson(
    title = title,
    description = null,
    icon = null,
    type = PaEMissionTypeJson.EVENT,
    arguments = JsonObject(),
    units = 5,
    progress = null,
    evidenceSource = evidenceSource,
  )

  private fun responseJson(
    requiresUsageAccess: Boolean?,
    requiresMmp: Boolean?,
    missions: List<PaEMissionJson> = listOf(missionJson("Level 5", "mmp")),
  ) = PaEMissionsJson(
    checkpoints = emptyList(),
    missions = missions,
    campaignId = 17,
    mmpClickUrl = null,
    attribution = null,
    requiresUsageAccess = requiresUsageAccess,
    requiresMmp = requiresMmp,
  )

  private fun repository(api: PaECampaignsApi, dao: PaeMissionDao = FakeDao()) =
    DefaultPaEMissionsRepository(api, dao, Dispatchers.Unconfined)

  @Test
  fun `A fetch carries the campaign's requirements and the missions' evidence sources`() =
    coScenario {
      m Given "a backend that says the campaign needs usage access and an MMP"
      val api = FakeApi(
        responseJson(
          requiresUsageAccess = true,
          requiresMmp = true,
          missions = listOf(
            missionJson("Play 10 min", "aptoide_usage"),
            missionJson("Level 5", "MMP"),
          ),
        )
      )

      m When "the missions are fetched"
      val missions =
        repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

      m Then "the requirements and evidence sources are in the domain model"
      assertEquals(true, missions.requiresUsageAccess)
      assertEquals(true, missions.requiresMmp)
      assertEquals(
        listOf(PaEEvidenceSource.APTOIDE_USAGE, PaEEvidenceSource.MMP),
        missions.missions.map { it.evidenceSource },
      )
    }

  @Test
  fun `An older backend leaves the requirements and evidence sources unknown`() = coScenario {
    m Given "a backend response without the AND-900 fields"
    val api = FakeApi(
      responseJson(
        requiresUsageAccess = null,
        requiresMmp = null,
        missions = listOf(missionJson("Level 5", evidenceSource = null)),
      )
    )

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "nothing is made up"
    assertNull(missions.requiresUsageAccess)
    assertNull(missions.requiresMmp)
    assertNull(missions.missions.single().evidenceSource)
  }

  @Test
  fun `An evidence source this app doesn't know maps to none`() = coScenario {
    m Given "a newer backend with a new evidence source"
    val api = FakeApi(
      responseJson(
        requiresUsageAccess = false,
        requiresMmp = false,
        missions = listOf(missionJson("Level 5", "blockchain")),
      )
    )

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "the mission is kept with no evidence source"
    assertNull(missions.missions.single().evidenceSource)
  }

  @Test
  fun `A cached read carries the requirements of the last fetch`() = coScenario {
    m Given "missions fetched once from a backend that says usage access is required"
    val api = FakeApi(responseJson(requiresUsageAccess = true, requiresMmp = false))
    val repository = repository(api)
    repository.getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m When "the missions are read again without forcing a refresh"
    val cached = repository.getCampaignMissions("com.game", forceRefresh = false).getOrThrow()

    m Then "the cache served them with the backend's answer"
    assertEquals(1, api.calls)
    assertEquals(true, cached.requiresUsageAccess)
    assertEquals(false, cached.requiresMmp)
    assertTrue(cached.missions.isNotEmpty())
  }

  @Test
  fun `A cached read of another game does not borrow the requirements`() = coScenario {
    m Given "one game fetched and another game's rows already in the cache"
    val dao = FakeDao()
    val api = FakeApi(responseJson(requiresUsageAccess = true, requiresMmp = true))
    val repository = repository(api, dao)
    repository.getCampaignMissions("com.game.one", forceRefresh = true).getOrThrow()
    dao.rows += dao.rows.map { it.copy(packageName = "com.game.two") }

    m When "the other game's missions are read from the cache"
    val cached = repository.getCachedMissions("com.game.two")

    m Then "its requirements are unknown"
    assertFalse(cached == null)
    assertNull(cached?.requiresUsageAccess)
    assertNull(cached?.requiresMmp)
  }
}
