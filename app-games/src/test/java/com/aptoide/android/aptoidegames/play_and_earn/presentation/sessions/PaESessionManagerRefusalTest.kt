package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusal
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalCode
import cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.play_and_earn.sessions.data.PaESessionsRepository
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionEndInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionInfo
import cm.aptoide.pt.play_and_earn.sessions.domain.SessionStartInfo
import cm.aptoide.pt.test.gherkin.coScenario
import com.aptoide.android.aptoidegames.analytics.AnalyticsSender
import com.aptoide.android.aptoidegames.analytics.GenericAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESession
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESessionRefusals
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaESessionStartResult
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.missions.PaEMissionManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.missions.PlayTimeMissionChecker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.IOException

// AND-950: what a refused session start, or a restricted heartbeat, does to the sessions.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaESessionManagerRefusalTest {

  private class FakeSessionsRepository : PaESessionsRepository {
    var startFailure: Throwable? = null
    var heartbeatFailure: Throwable? = null
    var starts = 0
    val heartbeats = mutableListOf<String>()

    override suspend fun startSession(packageName: String): Result<SessionStartInfo> {
      starts++
      return startFailure?.let { Result.failure(it) }
        ?: Result.success(SessionStartInfo(sessionId = "session-$starts", ttl = 180))
    }

    override suspend fun heartbeatSession(
      sessionId: String,
      packageName: String,
      sequence: Int,
      seconds: Int
    ): Result<SessionInfo> {
      heartbeats += packageName
      return heartbeatFailure?.let { Result.failure(it) }
        ?: Result.success(SessionInfo(appliedSeconds = seconds, ttl = 180, events = emptyList()))
    }

    override suspend fun endSession(sessionId: String, packageName: String) =
      Result.failure<SessionEndInfo>(UnsupportedOperationException())
  }

  private class FakeMissionsRepository : PaEMissionsRepository {
    override suspend fun getCampaignMissions(packageName: String, forceRefresh: Boolean) =
      Result.success(PaEMissions(checkpoints = emptyList(), missions = emptyList()))

    override suspend fun getEventMissions() = Result.success(emptyList<PaEMission>())

    override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> =
      emptyFlow()

    override suspend fun getCachedMissions(packageName: String): PaEMissions? = null

    override suspend fun markMissionAsCompleted(packageName: String, missionTitle: String) = Unit
  }

  private val emptyDataStore = object : DataStore<Preferences> {
    override val data: Flow<Preferences> = flowOf(emptyPreferences())
    override suspend fun updateData(
      transform: suspend (t: Preferences) -> Preferences
    ): Preferences = transform(emptyPreferences())
  }

  private val sessions = FakeSessionsRepository()
  private val missions = FakeMissionsRepository()
  private val refusals = PaESessionRefusals()
  private val manager = PaESessionManager(
    paESessionsRepository = sessions,
    paeMissionsRepository = missions,
    paeMissionManager = PaEMissionManager(PlayTimeMissionChecker(), missions),
    paEPreferencesRepository = PaEPreferencesRepository(emptyDataStore),
    paEAnalytics = PaEAnalytics(GenericAnalytics(object : AnalyticsSender {})),
    paESessionRefusals = refusals,
  )

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = ["REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED"]
  )
  fun `A refused start is typed, kept for the Rewards tab and not asked again`(
    code: PaERefusalCode,
  ) = coScenario {
    m Given "the backend refuses the game's session with $code"
    sessions.startFailure = PaERefusalException(PaERefusal(code))

    m When "the session is created twice"
    val first = manager.createSession(GAME)
    val second = manager.createSession(GAME)

    m Then "both are refused with the code, the server was asked once, the tab can show it"
    assertEquals(PaESessionStartResult.Refused(PaERefusal(code)), first)
    assertEquals(first, second)
    assertEquals(1, sessions.starts)
    assertTrue(manager.activeSessions.isEmpty())
    assertEquals(PaERefusal(code), refusals.observe(GAME).first())
    assertEquals(PaERefusal(code), manager.refusalOf(GAME))
    assertFalse(manager.isAccountRestricted)
  }

  @Test
  fun `A failed start is retried and shows nothing new`() = coScenario {
    m Given "the session start fails on the network"
    sessions.startFailure = IOException("offline")

    m When "the session is created twice"
    val first = manager.createSession(GAME)
    val second = manager.createSession(GAME)

    m Then "both failed, the server was asked each time, and no refusal is kept"
    assertEquals(PaESessionStartResult.Failed, first)
    assertEquals(PaESessionStartResult.Failed, second)
    assertEquals(2, sessions.starts)
    assertNull(refusals.observe(GAME).first())
    assertNull(manager.refusalOf(GAME))
  }

  @Test
  fun `A started session is typed as started, then as already active`() = coScenario {
    m Given "the backend accepts the session"

    m When "the session is created twice"
    val first = manager.createSession(GAME)
    val second = manager.createSession(GAME)

    m Then "the first starts it and the second finds it running"
    assertEquals(PaESessionStartResult.Started, first)
    assertEquals(PaESessionStartResult.AlreadyActive, second)
    assertEquals(1, manager.activeSessions.size)
  }

  @Test
  fun `An accepted start clears the game's refusal`() = coScenario {
    m Given "a region refusal kept from an earlier tracking run"
    sessions.startFailure = PaERefusalException(PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))
    manager.createSession(GAME)
    manager.clearAllSessions()
    sessions.startFailure = null

    m When "a new run starts the session and the backend accepts it"
    val result = manager.createSession(GAME)

    m Then "it starts and the Rewards tab no longer has a refusal to show"
    assertEquals(PaESessionStartResult.Started, result)
    assertNull(refusals.observe(GAME).first())
    assertNull(manager.refusalOf(GAME))
  }

  @Test
  fun `A restricted start restricts the account`() = coScenario {
    m Given "another game's session running and the account restricted on the backend"
    manager.activeSessions += session(OTHER_GAME)
    sessions.startFailure =
      PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m When "the game's session is created"
    val result = manager.createSession(GAME)

    m Then "it is refused, the account is restricted and no session is left"
    assertEquals(
      PaESessionStartResult.Refused(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED)),
      result
    )
    assertTrue(manager.isAccountRestricted)
    assertTrue(manager.activeSessions.isEmpty())
  }

  @Test
  fun `A restricted heartbeat drops every session, with no retry and no new session`() =
    coScenario {
      m Given "the game in front and another game with sessions, the account restricted"
      manager.activeSessions += session(GAME)
      manager.activeSessions += session(OTHER_GAME)
      sessions.heartbeatFailure =
        PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

      m When "the sessions sync, sync again once due, and the game's session is recreated"
      manager.syncSessions(GAME, playedMillis = 20_000)
      manager.syncSessions(GAME, playedMillis = 20_000)
      val recreated = manager.createSession(GAME)

      m Then "one heartbeat was sent, the tracking service is told to stop, nothing restarts"
      assertEquals(listOf(GAME), sessions.heartbeats)
      assertTrue(manager.isAccountRestricted)
      assertTrue(manager.activeSessions.isEmpty())
      assertEquals(
        PaESessionStartResult.Refused(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED)),
        recreated
      )
      assertEquals(0, sessions.starts)
      assertEquals(
        PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED),
        refusals.observe(GAME).first()
      )
    }

  @Test
  fun `A restricted status heartbeat stops the status mode`() = coScenario {
    m Given "a status-mode session and the account restricted"
    manager.activeSessions += session(GAME)
    sessions.heartbeatFailure =
      PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m When "the status heartbeat is sent"
    val alive = manager.heartbeatStatus(GAME)

    m Then "the session is gone, the account restricted, and the refusal says why"
    assertFalse(alive)
    assertTrue(manager.isAccountRestricted)
    assertTrue(manager.activeSessions.isEmpty())
    assertEquals(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED), manager.refusalOf(GAME))
  }

  @Test
  fun `A failed heartbeat keeps the session as before`() = coScenario {
    m Given "the game in front with a session, and a heartbeat failing on the network"
    manager.activeSessions += session(GAME)
    sessions.heartbeatFailure = IOException("offline")

    m When "the sessions sync"
    manager.syncSessions(GAME, playedMillis = 20_000)

    m Then "the session stays for the next beat and nothing is restricted"
    assertEquals(1, manager.activeSessions.size)
    assertFalse(manager.isAccountRestricted)
    assertNull(manager.refusalOf(GAME))
  }

  @Test
  fun `Clearing the sessions lets a new run ask again`() = coScenario {
    m Given "a restricted account"
    sessions.startFailure =
      PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))
    manager.createSession(GAME)

    m When "the sessions are cleared, as when the service stops, and the next run starts one"
    manager.clearAllSessions()
    sessions.startFailure = null
    val result = manager.createSession(GAME)

    m Then "the backend is asked again and its answer counts"
    assertFalse(manager.isAccountRestricted)
    assertEquals(PaESessionStartResult.Started, result)
    assertEquals(2, sessions.starts)
  }

  /** A session due for a heartbeat at the next sync. */
  private fun session(packageName: String) = PaESession(
    sessionId = "session-$packageName",
    packageName = packageName,
    ttlSeconds = 180,
    missions = null,
    heartbeatIntervalSeconds = 15,
  ).also { it.lastSyncTime = 0L }

  private companion object {
    const val GAME = "com.game"
    const val OTHER_GAME = "com.other.game"
  }
}
