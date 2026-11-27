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

// AND-950: what a refused session start, or a refused heartbeat, does to the sessions, and
// when the tracking service and the status heartbeat stop.
@OptIn(ExperimentalCoroutinesApi::class)
internal class PaESessionManagerRefusalTest {

  private class FakeSessionsRepository : PaESessionsRepository {
    var startFailure: Throwable? = null
    var heartbeatFailure: Throwable? = null
    var starts = 0
    val heartbeats = mutableListOf<Triple<String, Int, Int>>() // package, sequence, seconds

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
      heartbeats += Triple(packageName, sequence, seconds)
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

  private var now = 1_000_000L
  private val sessions = FakeSessionsRepository()
  private val missions = FakeMissionsRepository()
  private val refusals = PaESessionRefusals { now }
  private val manager = PaESessionManager(
    paESessionsRepository = sessions,
    paeMissionsRepository = missions,
    paeMissionManager = PaEMissionManager(PlayTimeMissionChecker(), missions),
    paEPreferencesRepository = PaEPreferencesRepository(emptyDataStore),
    paEAnalytics = PaEAnalytics(GenericAnalytics(object : AnalyticsSender {})),
    paESessionRefusals = refusals,
  )

  // ------------------------------------------------------------------------- session start

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = [
      "ACCOUNT_RESTRICTED", "REGION_NOT_SUPPORTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED",
    ]
  )
  fun `A play-blocking start refusal is typed, shown and not asked again for 10 minutes`(
    code: PaERefusalCode,
  ) = coScenario {
    m Given "the backend refuses the game's session with $code"
    refuseStart(code)

    m When "the session is created, then again on the next poll"
    val first = manager.createSession(GAME)
    val second = manager.createSession(GAME)

    m Then "both are refused with the code, the server was asked once, the tab shows it"
    assertEquals(PaESessionStartResult.Refused(PaERefusal(code)), first)
    assertEquals(first, second)
    assertEquals(1, sessions.starts)
    assertTrue(manager.activeSessions.isEmpty())
    assertEquals(PaERefusal(code), refusals.observe(GAME).first())
    assertTrue(manager.shouldStopStatusMode(GAME))
  }

  @Test
  fun `A refused game is asked again after 10 minutes`() = coScenario {
    m Given "the game refused for the user's region, who then travels"
    refuseStart(PaERefusalCode.REGION_NOT_SUPPORTED)
    manager.createSession(GAME)
    sessions.startFailure = null

    m When "the next poll comes 10 minutes later"
    now += PaESessionRefusals.ASK_AGAIN_AFTER_MILLIS
    val result = manager.createSession(GAME)

    m Then "the backend is asked again, accepts, and the refusal is gone"
    assertEquals(PaESessionStartResult.Started, result)
    assertEquals(2, sessions.starts)
    assertNull(refusals.observe(GAME).first())
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = ["TOO_MANY_ATTEMPTS", "INSTALL_LIMIT", "SIGN_IN_BONUS_DEVICE_LIMIT"]
  )
  fun `A limit on a start is a plain failure, asked again on the next poll`(
    code: PaERefusalCode,
  ) = coScenario {
    m Given "the backend answers the session start with a 429 $code"
    refuseStart(code)

    m When "the session is created, then again on the next poll"
    val first = manager.createSession(GAME)
    val second = manager.createSession(GAME)

    m Then "both failed as before, the server was asked each time, nothing is shown"
    assertEquals(PaESessionStartResult.Failed, first)
    assertEquals(PaESessionStartResult.Failed, second)
    assertEquals(2, sessions.starts)
    assertNull(refusals.observe(GAME).first())
    assertFalse(manager.shouldStopService())
    assertFalse(manager.shouldStopStatusMode(GAME))
  }

  @Test
  fun `A failed start is retried and shows nothing`() = coScenario {
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

  @ParameterizedTest(name = "{0}")
  @EnumSource(
    PaERefusalCode::class,
    names = ["ACCOUNT_RESTRICTED", "DEVICE_NOT_SUPPORTED", "UPDATE_REQUIRED"]
  )
  fun `An every-game start refusal stops the service and shows on every game`(
    code: PaERefusalCode,
  ) = coScenario {
    m Given "another game's session running and the backend refusing with $code"
    manager.activeSessions += session(OTHER_GAME)
    refuseStart(code)

    m When "the game's session is created"
    manager.createSession(GAME)

    m Then "no session is left, the service is told to stop, every Rewards tab shows it"
    assertTrue(manager.activeSessions.isEmpty())
    assertTrue(manager.shouldStopService())
    assertEquals(PaERefusal(code), refusals.observe(OTHER_GAME).first())
  }

  // AND-964: a VPN goes away when the user turns it off. Stopping the service would leave
  // nothing to ask again until the user tapped Play in the app; it keeps tracking instead.
  @Test
  fun `A VPN start refusal keeps the service and the status mode going`() = coScenario {
    m Given "another game's session running and the backend refusing for a VPN"
    manager.activeSessions += session(OTHER_GAME)
    refuseStart(PaERefusalCode.VPN_NOT_ALLOWED)

    m When "the game's session is created"
    val result = manager.createSession(GAME)

    m Then "refused on every game, no session kept, but nothing is told to stop"
    assertEquals(
      PaESessionStartResult.Refused(PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED)),
      result
    )
    assertTrue(manager.activeSessions.isEmpty())
    assertFalse(manager.shouldStopService())
    assertFalse(manager.shouldStopStatusMode(GAME))
    assertTrue(manager.isWaitingForTheUser(GAME))
    assertEquals(
      PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED),
      refusals.observe(OTHER_GAME).first()
    )
  }

  @Test
  fun `A VPN refusal asks the backend again once a minute, then starts`() = coScenario {
    m Given "the game refused for a VPN"
    refuseStart(PaERefusalCode.VPN_NOT_ALLOWED)
    manager.createSession(GAME)

    m When "the 10-second polls go on for a minute, the VPN is turned off, a poll comes"
    repeat(5) {
      now += 10_000L
      manager.createSession(GAME)
    }
    val startsWithinTheMinute = sessions.starts
    now += 10_000L
    val stillVpn = manager.createSession(GAME)
    sessions.startFailure = null
    now += PaESessionRefusals.VPN_ASK_AGAIN_AFTER_MILLIS
    val afterVpnOff = manager.createSession(GAME)

    m Then "asked once in the first minute, once at its end, then started and cleared"
    assertEquals(1, startsWithinTheMinute)
    assertEquals(
      PaESessionStartResult.Refused(PaERefusal(PaERefusalCode.VPN_NOT_ALLOWED)),
      stillVpn
    )
    assertEquals(PaESessionStartResult.Started, afterVpnOff)
    assertEquals(3, sessions.starts)
    assertNull(refusals.observe(GAME).first())
    assertFalse(manager.isWaitingForTheUser(GAME))
  }

  @Test
  fun `A region start refusal does not stop the service`() = coScenario {
    m Given "another game's session running and the game refused for the region"
    manager.activeSessions += session(OTHER_GAME)
    refuseStart(PaERefusalCode.REGION_NOT_SUPPORTED)

    m When "the game's session is created"
    manager.createSession(GAME)

    m Then "the other game keeps its session and tracking goes on"
    assertEquals(listOf(OTHER_GAME), manager.activeSessions.map { it.packageName })
    assertFalse(manager.shouldStopService())
    assertFalse(manager.shouldStopStatusMode(OTHER_GAME))
    assertNull(refusals.observe(OTHER_GAME).first())
  }

  // ----------------------------------------------------------------------------- heartbeat

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

      m Then "one heartbeat was sent, the service is told to stop, nothing restarts"
      assertEquals(listOf(GAME), sessions.heartbeats.map { it.first })
      assertTrue(manager.shouldStopService())
      assertTrue(manager.activeSessions.isEmpty())
      assertEquals(
        PaESessionStartResult.Refused(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED)),
        recreated
      )
      assertEquals(0, sessions.starts)
      assertEquals(
        PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED),
        refusals.observe(OTHER_GAME).first()
      )
    }

  @Test
  fun `A region heartbeat refusal drops that game's session only`() = coScenario {
    m Given "the game in front and another game with sessions, the game refused for the region"
    manager.activeSessions += session(GAME)
    manager.activeSessions += session(OTHER_GAME)
    sessions.heartbeatFailure =
      PaERefusalException(PaERefusal(PaERefusalCode.REGION_NOT_SUPPORTED))

    m When "the sessions sync"
    manager.syncSessions(GAME, playedMillis = 20_000)

    m Then "the game's session is gone and tracking goes on"
    assertFalse(manager.activeSessions.any { it.packageName == GAME })
    assertFalse(manager.shouldStopService())
    assertTrue(manager.shouldStopStatusMode(GAME))
  }

  @ParameterizedTest(name = "{0}")
  @EnumSource(PaERefusalCode::class, names = ["TOO_MANY_ATTEMPTS", "INSTALL_LIMIT"])
  fun `A limit on a heartbeat keeps the session and its unsent time`(code: PaERefusalCode) =
    coScenario {
      m Given "the game in front, and its heartbeat answered with a 429 $code"
      val session = session(GAME)
      manager.activeSessions += session
      sessions.heartbeatFailure = PaERefusalException(PaERefusal(code))

      m When "the sessions sync with 20 s of play, then again once due"
      manager.syncSessions(GAME, playedMillis = 20_000)
      val unsentAfterFailure = session.unsentPlayMillis
      session.lastSyncTime = 0L
      manager.syncSessions(GAME, playedMillis = 10_000)

      m Then "as any failure: kept, time kept, sequence bumped, retried, nothing shown"
      assertEquals(20_000L, unsentAfterFailure)
      assertEquals(listOf(Triple(GAME, 0, 20), Triple(GAME, 1, 30)), sessions.heartbeats)
      assertEquals(listOf(session), manager.activeSessions)
      assertFalse(manager.shouldStopService())
      assertNull(refusals.observe(GAME).first())
    }

  @Test
  fun `A failed heartbeat keeps the session as before`() = coScenario {
    m Given "the game in front with a session, and a heartbeat failing on the network"
    manager.activeSessions += session(GAME)
    sessions.heartbeatFailure = IOException("offline")

    m When "the sessions sync"
    manager.syncSessions(GAME, playedMillis = 20_000)

    m Then "the session stays for the next beat and nothing stops"
    assertEquals(1, manager.activeSessions.size)
    assertFalse(manager.shouldStopService())
    assertNull(manager.refusalOf(GAME))
  }

  @Test
  fun `A restricted status heartbeat stops the status mode`() = coScenario {
    m Given "a status-mode session and the account restricted"
    manager.activeSessions += session(GAME)
    sessions.heartbeatFailure =
      PaERefusalException(PaERefusal(PaERefusalCode.ACCOUNT_RESTRICTED))

    m When "the status heartbeat is sent"
    val alive = manager.heartbeatStatus(GAME)

    m Then "the session is gone and the status mode is told to stop"
    assertFalse(alive)
    assertTrue(manager.activeSessions.isEmpty())
    assertTrue(manager.shouldStopStatusMode(GAME))
  }

  @Test
  fun `A limit on a status heartbeat keeps it going`() = coScenario {
    m Given "a status-mode session and a heartbeat answered with a 429"
    manager.activeSessions += session(GAME)
    sessions.heartbeatFailure =
      PaERefusalException(PaERefusal(PaERefusalCode.TOO_MANY_ATTEMPTS))

    m When "the status heartbeat is sent"
    val alive = manager.heartbeatStatus(GAME)

    m Then "as any failure: the session stays and the status mode goes on"
    assertTrue(alive)
    assertEquals(1, manager.activeSessions.size)
    assertFalse(manager.shouldStopStatusMode(GAME))
  }

  // ------------------------------------------------------------------------- service stop

  @Test
  fun `A service stopped for any other reason forgets the refusals`() = coScenario {
    m Given "a region refusal and a device refusal"
    refuseStart(PaERefusalCode.REGION_NOT_SUPPORTED)
    manager.createSession(GAME)
    refuseStart(PaERefusalCode.DEVICE_NOT_SUPPORTED)
    manager.createSession(OTHER_GAME)

    m When "the service stops (idle, flags off) and clears all sessions"
    manager.clearAllSessions()

    m Then "nothing is shown or blocks the next run"
    assertNull(refusals.observe(GAME).first())
    assertNull(refusals.observe(OTHER_GAME).first())
    assertFalse(manager.shouldStopService())
  }

  @Test
  fun `A service stopped by a refusal keeps it for the Rewards tab`() = coScenario {
    m Given "a game's session running and an update refusal"
    manager.activeSessions += session(OTHER_GAME)
    refuseStart(PaERefusalCode.UPDATE_REQUIRED)
    manager.createSession(GAME)

    m When "the service stops because of it"
    manager.dropSessionsKeepingRefusals()

    m Then "no session is left, the update message stays, a new run stops too"
    assertTrue(manager.activeSessions.isEmpty())
    assertEquals(
      PaERefusal(PaERefusalCode.UPDATE_REQUIRED),
      refusals.observe(OTHER_GAME).first()
    )
    assertTrue(manager.shouldStopService())
  }

  private fun refuseStart(code: PaERefusalCode) {
    sessions.startFailure = PaERefusalException(PaERefusal(code))
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
