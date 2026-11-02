package cm.aptoide.pt.campaigns.data

import cm.aptoide.pt.campaigns.data.database.PaeMissionDao
import cm.aptoide.pt.campaigns.data.database.model.PaEMissionEntity
import cm.aptoide.pt.campaigns.data.model.PaECampaignJson
import cm.aptoide.pt.campaigns.data.model.PaEEventMissionsJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionTypeJson
import cm.aptoide.pt.campaigns.data.model.PaEMissionsJson
import cm.aptoide.pt.campaigns.domain.PaETracking
import cm.aptoide.pt.campaigns.domain.PaEMmpPlatform
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.test.gherkin.coScenario
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

// AND-915/AND-901: the campaign's tracking reaches the domain from the network and survives a cached
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

  private fun responseJson(tracking: String?, mmp: String? = null) = PaEMissionsJson(
    checkpoints = emptyList(),
    missions = listOf(missionJson("Level 5")),
    campaignId = 17,
    mmpClickUrl = null,
    attribution = null,
    tracking = tracking,
    mmp = mmp,
  )

  private fun repository(api: PaECampaignsApi, dao: PaeMissionDao = FakeDao()) =
    DefaultPaEMissionsRepository(api, dao, Dispatchers.Unconfined)

  private suspend fun fetch(api: PaECampaignsApi) =
    repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

  @Test
  fun `A fetch carries the tracking`() = coScenario {
    m Given "a backend that says the campaign is play-time"
    val api = FakeApi(responseJson(tracking = "APTOIDE"))

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "the type is in the domain model"
    assertEquals(PaETracking.APTOIDE, missions.tracking)
  }

  @Test
  fun `An older backend leaves the type unknown`() = coScenario {
    m Given "a backend response without the type"
    val api = FakeApi(responseJson(tracking = null))

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "nothing is made up"
    assertNull(missions.tracking)
  }

  @Test
  fun `A type this app doesn't know maps to none`() = coScenario {
    m Given "a newer backend with a new tracking"
    val api = FakeApi(responseJson(tracking = "GOOGLE"))

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "the missions are kept with no type"
    assertNull(missions.tracking)
    assertTrue(missions.missions.isNotEmpty())
  }

  @Test
  fun `A cached read carries the type of the last fetch`() = coScenario {
    m Given "missions fetched once from a backend that says event"
    val api = FakeApi(responseJson(tracking = "MMP"))
    val repository = repository(api)
    repository.getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m When "the missions are read again without forcing a refresh"
    val cached = repository.getCampaignMissions("com.game", forceRefresh = false).getOrThrow()

    m Then "the cache served them with the backend's type"
    assertEquals(1, api.calls)
    assertEquals(PaETracking.MMP, cached.tracking)
    assertTrue(cached.missions.isNotEmpty())
  }

  @Test
  fun `A fetch without a type forgets the one remembered before`() = coScenario {
    m Given "a game fetched as play-time, then fetched again from a backend without the type"
    val api = FakeApi(responseJson(tracking = "APTOIDE"))
    val repository = repository(api)
    repository.getCampaignMissions("com.game", forceRefresh = true).getOrThrow()
    api.response = responseJson(tracking = null)
    repository.getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m When "the missions are read from the cache"
    val cached = repository.getCampaignMissions("com.game", forceRefresh = false).getOrThrow()

    m Then "the stale type is gone"
    assertNull(cached.tracking)
  }

  @Test
  fun `A cached read of another game does not borrow the type`() = coScenario {
    m Given "one game fetched and another game's rows already in the cache"
    val dao = FakeDao()
    val api = FakeApi(responseJson(tracking = "APTOIDE"))
    val repository = repository(api, dao)
    repository.getCampaignMissions("com.game.one", forceRefresh = true).getOrThrow()
    dao.rows += dao.rows.map { it.copy(packageName = "com.game.two") }

    m When "the other game's missions are read from the cache"
    val cached = repository.getCachedMissions("com.game.two")

    m Then "its type is unknown"
    assertFalse(cached == null)
    assertNull(cached?.tracking)
  }

  // AND-912: which MMP the click link belongs to, as the backend says it (AND-916).
  @Test
  fun `A fetch carries the campaign's MMP, in any case`() = coScenario {
    m Given "a backend that says the campaign is an Adjust one"
    val api = FakeApi(responseJson(tracking = "MMP", mmp = "adjust"))

    m When "the missions are fetched"
    val missions = repository(api).getCampaignMissions("com.game", forceRefresh = true).getOrThrow()

    m Then "the MMP is Adjust"
    assertEquals(PaEMmpPlatform.ADJUST, missions.mmp)
  }

  @Test
  fun `An older backend, or an MMP this app doesn't know, leaves the MMP unknown`() = coScenario {
    m Given "a response without mmp and one with a new MMP"
    val without = FakeApi(responseJson(tracking = "MMP"))
    val unknown = FakeApi(responseJson(tracking = "MMP", mmp = "BRANCH"))

    m When "the missions are fetched"
    val older = fetch(without)
    val newer = fetch(unknown)

    m Then "neither makes one up, the missions are kept"
    assertNull(older.mmp)
    assertNull(newer.mmp)
    assertTrue(newer.missions.isNotEmpty())
  }
}
