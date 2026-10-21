package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.database.PaeMissionDao
import cm.aptoide.pt.campaigns.data.database.model.PaEMissionEntity
import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEEventMissionsJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionTypeJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import cm.aptoide.pt.campaigns.domain.PaECampaignType
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.test.gherkin.coScenario
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-915/AND-901: the campaign type reaches the domain from the network and survives a cached
// read within the process (the Room cache keeps only the missions).
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

  private fun missionJson(title: String) = PaEMissionJson(
    title = title,
    description = null,
    icon = null,
    type = PaEMissionTypeJson.EVENT,
    arguments = JsonObject(),
    units = 5,
    progress = null,
  )

  private fun responseJson(type: String?) = PaEMissionsJson(
    checkpoints = emptyList(),
    missions = listOf(missionJson("Level 5")),
    campaignId = 17,
    mmpClickUrl = null,
    attribution = null,
    type = type,
  )

  private fun repository(api: PaECampaignsApi, dao: PaeMissionDao = FakeDao()) =
    DefaultPaEMissionsRepository(api, dao, Dispatchers.Unconfined)

  @Test
  fun `A fetch carries the campaign type`() = coScenario {
    m Given "a backend that says the campaign is play-time"
    val api = FakeApi(responseJson(type = "PLAY_TIME"))

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "the type is in the domain model"
    assertEquals(PaECampaignType.PLAY_TIME, missions.type)
  }

  @Test
  fun `An older backend leaves the type unknown`() = coScenario {
    m Given "a backend response without the type"
    val api = FakeApi(responseJson(type = null))

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "nothing is made up"
    assertNull(missions.type)
  }

  @Test
  fun `A type this app doesn't know maps to none`() = coScenario {
    m Given "a newer backend with a new campaign type"
    val api = FakeApi(responseJson(type = "HYBRID"))

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "the missions are kept with no type"
    assertNull(missions.type)
    assertTrue(missions.missions.isNotEmpty())
  }

  @Test
  fun `A cached read carries the type of the last fetch`() = coScenario {
    m Given "missions fetched once from a backend that says event"
    val api = FakeApi(responseJson(type = "EVENT"))
    val repository = repository(api)
    repository.getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m When "the missions are read again without forcing a refresh"
    val cached = repository.getCampaignMissions("com.game", forceRefresh = false).getOrThrow()

    m Then "the cache served them with the backend's type"
    assertEquals(1, api.calls)
    assertEquals(PaECampaignType.EVENT, cached.type)
    assertTrue(cached.missions.isNotEmpty())
  }

  @Test
  fun `A fetch without a type forgets the one remembered before`() = coScenario {
    m Given "a game fetched as play-time, then fetched again from a backend without the type"
    val api = FakeApi(responseJson(type = "PLAY_TIME"))
    val repository = repository(api)
    repository.getCampaignMissions("com.game", forceRefresh = true).getOrThrow()
    api.response = responseJson(type = null)
    repository.getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m When "the missions are read from the cache"
    val cached = repository.getCampaignMissions("com.game", forceRefresh = false).getOrThrow()

    m Then "the stale type is gone"
    assertNull(cached.type)
  }

  @Test
  fun `A cached read of another game does not borrow the type`() = coScenario {
    m Given "one game fetched and another game's rows already in the cache"
    val dao = FakeDao()
    val api = FakeApi(responseJson(type = "PLAY_TIME"))
    val repository = repository(api, dao)
    repository.getCampaignMissions("com.game.one", forceRefresh = true).getOrThrow()
    dao.rows += dao.rows.map { it.copy(packageName = "com.game.two") }

    m When "the other game's missions are read from the cache"
    val cached = repository.getCachedMissions("com.game.two")

    m Then "its type is unknown"
    assertFalse(cached == null)
    assertNull(cached?.type)
  }
}
