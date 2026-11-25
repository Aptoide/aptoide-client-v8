package com.aptoide.android.aptoidegames.play_and_earn.presentation.sessions

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
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
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.missions.PaEMissionManager
import com.aptoide.android.aptoidegames.play_and_earn.presentation.missions.PlayTimeMissionChecker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
internal class PaESessionManagerPlayTimeTest {

  private class FakeSessionsRepository : PaESessionsRepository {
    val heartbeats = mutableListOf<Pair<Int, Int>>() // sequence to seconds
    var failNext = false

    override suspend fun startSession(packageName: String): Result<SessionStartInfo> =
      Result.failure(UnsupportedOperationException())

    override suspend fun heartbeatSession(
      sessionId: String,
      packageName: String,
      sequence: Int,
      seconds: Int
    ): Result<SessionInfo> {
      heartbeats += sequence to seconds
      if (failNext) {
        failNext = false
        return Result.failure(IOException("offline"))
      }
      return Result.success(SessionInfo(appliedSeconds = seconds, ttl = 180, events = emptyList()))
    }

    override suspend fun endSession(sessionId: String, packageName: String) =
      Result.failure<SessionEndInfo>(UnsupportedOperationException())
  }

  private class FakeMissionsRepository : PaEMissionsRepository {
    override suspend fun getCampaignMissions(packageName: String, forceRefresh: Boolean) =
      Result.failure<PaEMissions>(UnsupportedOperationException())

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
  private val manager = PaESessionManager(
    paESessionsRepository = sessions,
    paeMissionsRepository = missions,
    paeMissionManager = PaEMissionManager(PlayTimeMissionChecker(), missions),
    paEPreferencesRepository = PaEPreferencesRepository(emptyDataStore),
    paEAnalytics = PaEAnalytics(GenericAnalytics(object : AnalyticsSender {})),
  )

  /** A session due for a heartbeat at the next sync. */
  private val session = PaESession(
    sessionId = "session",
    packageName = GAME,
    ttlSeconds = 180,
    missions = null,
    heartbeatIntervalSeconds = 15,
  ).also {
    it.lastSyncTime = 0L
    manager.activeSessions.add(it)
  }

  @Test
  fun `A heartbeat claims the whole measured seconds, at most 60`() = coScenario {
    m Given "70.5 s of play measured for the game in front"
    session.addPlayTime(60_000)

    m When "the sessions sync with another 10.5 s"
    manager.syncSessions(GAME, playedMillis = 10_500)

    m Then "60 s are claimed and the 10.5 s left wait for the next beat"
    assertEquals(listOf(0 to 60), sessions.heartbeats)
    assertEquals(10_500L, session.unsentPlayMillis)
    assertEquals(1, session.syncSequence)
  }

  @Test
  fun `A successful heartbeat subtracts only what it claimed`() = coScenario {
    m Given "a game in front"

    m When "the sessions sync with 20.4 s of play"
    manager.syncSessions(GAME, playedMillis = 20_400)

    m Then "20 s are claimed and 0.4 s carry over"
    assertEquals(listOf(0 to 20), sessions.heartbeats)
    assertEquals(400L, session.unsentPlayMillis)
    assertEquals(20, session.totalSessionTime)
  }

  @Test
  fun `A failed heartbeat keeps the time and still bumps the sequence`() = coScenario {
    m Given "a heartbeat that will fail on the network"
    sessions.failNext = true

    m When "the sessions sync with 20 s of play, then again once due with 10 s more"
    manager.syncSessions(GAME, playedMillis = 20_000)
    val afterFailure = session.unsentPlayMillis
    session.lastSyncTime = 0L
    manager.syncSessions(GAME, playedMillis = 10_000)

    m Then "the time stays for the next beat, which uses a new sequence and claims it all"
    assertEquals(20_000L, afterFailure)
    assertEquals(listOf(0 to 20, 1 to 30), sessions.heartbeats)
    assertEquals(0L, session.unsentPlayMillis)
    assertEquals(2, session.syncSequence)
  }

  @Test
  fun `A game not in front gets no time and no heartbeat`() = coScenario {
    m Given "a session for a game that is not in front"

    m When "the sessions sync for another app with 10 s"
    manager.syncSessions(OTHER, playedMillis = 10_000)

    m Then "nothing is added or sent"
    assertEquals(emptyList<Pair<Int, Int>>(), sessions.heartbeats)
    assertEquals(0L, session.unsentPlayMillis)
  }

  private companion object {
    const val GAME = "com.game"
    const val OTHER = "com.other"
  }
}
