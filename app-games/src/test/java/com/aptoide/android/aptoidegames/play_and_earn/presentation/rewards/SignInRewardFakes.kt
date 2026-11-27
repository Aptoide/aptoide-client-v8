package com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionType
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.play_and_earn.events.data.EventsRepository
import cm.aptoide.pt.play_and_earn.events.domain.EventType
import cm.aptoide.pt.play_and_earn.exchange.data.ExchangeRepository
import cm.aptoide.pt.play_and_earn.exchange.domain.ExchangeRate
import cm.aptoide.pt.play_and_earn.exchange.domain.GetExchangeRateUseCase
import cm.aptoide.pt.play_and_earn.exchange.domain.RedeemType
import com.aptoide.android.aptoidegames.play_and_earn.WalletUnitsRefresher
import com.aptoide.android.aptoidegames.play_and_earn.data.InMemoryPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.PaESignInBonusRefusal
import com.google.gson.JsonObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import java.io.IOException

/** Answers each claim with the next queued result (success when none), after [gate] if set. */
internal class FakeEventsRepository : EventsRepository {
  val results = ArrayDeque<Result<Unit>>()
  var gate: CompletableDeferred<Unit>? = null
  var sent = 0

  override suspend fun submitEvent(eventType: EventType): Result<Unit> {
    sent++
    gate?.await()
    return results.removeFirstOrNull() ?: Result.success(Unit)
  }
}

/** Offers a FIRST_SIGN_IN mission worth 100 units, not completed. */
internal class FakeSignInMissionsRepository : PaEMissionsRepository {
  override suspend fun getCampaignMissions(packageName: String, forceRefresh: Boolean) =
    Result.success(PaEMissions(checkpoints = emptyList(), missions = emptyList()))

  override suspend fun getEventMissions() = Result.success(
    listOf(
      PaEMission(
        title = "Sign in",
        description = null,
        icon = null,
        type = PaEMissionType.EVENT,
        arguments = JsonObject().apply { addProperty("event_type", "FIRST_SIGN_IN") },
        units = 100,
        progress = null,
      )
    )
  )

  override fun observeCampaignMissions(packageName: String): Flow<Result<PaEMissions>> =
    emptyFlow()

  override suspend fun getCachedMissions(packageName: String): PaEMissions? = null

  override suspend fun markMissionAsCompleted(packageName: String, missionTitle: String) = Unit
}

/** No rate: the reward shows the default amount. */
internal class NoRateExchangeRepository : ExchangeRepository {
  override suspend fun exchangeUnits(email: String, redeemType: RedeemType, requestId: String) =
    Result.failure<Unit>(UnsupportedOperationException())

  override suspend fun getExchangeRate() =
    Result.failure<ExchangeRate>(UnsupportedOperationException())
}

/** Preferences kept in memory whose reads or writes can be made to fail as a broken disk would. */
internal class FlakyPreferencesDataStore : DataStore<Preferences> {
  private val delegate = InMemoryPreferencesDataStore()
  var failReads = false
  var failWrites = false

  override val data: Flow<Preferences> = flow {
    if (failReads) throw IOException("read failed")
    emitAll(delegate.data)
  }

  override suspend fun updateData(
    transform: suspend (t: Preferences) -> Preferences
  ): Preferences {
    if (failWrites) throw IOException("write failed")
    return delegate.updateData(transform)
  }
}

/** A repository whose work runs at once, cancelled when the test ends. */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun TestScope.signInRewardRepository(
  events: EventsRepository,
  bonusRefusal: PaESignInBonusRefusal,
) = SignInRewardRepository(
  eventsRepository = events,
  paeMissionsRepository = FakeSignInMissionsRepository(),
  getExchangeRateUseCase = GetExchangeRateUseCase(NoRateExchangeRepository()),
  walletUnitsRefresher = WalletUnitsRefresher(),
  bonusRefusal = bonusRefusal,
  scope = CoroutineScope(
    backgroundScope.coroutineContext + UnconfinedTestDispatcher(testScheduler)
  ),
)
