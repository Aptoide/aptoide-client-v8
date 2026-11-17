package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.data.PaEProgressRepository
import cm.aptoide.pt.campaigns.domain.PaEUserProgress
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import cm.aptoide.pt.wallet.wallet_info.data.WalletInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val LEADERBOARD_SIZE = 10

@HiltViewModel
class MyProgressViewModel @Inject constructor(
  @ApplicationContext private val context: Context,
  private val progressRepository: PaEProgressRepository,
  private val campaignsRepository: PaECampaignsRepository,
  private val missionsRepository: PaEMissionsRepository,
  private val walletInfoRepository: WalletInfoRepository,
  private val walletCoreDataSource: WalletCoreDataSource,
  private val claimedMissionsStore: PaEClaimedMissionsStore,
  private val exceptionHandler: ExceptionHandler,
) : ViewModel() {

  private val _uiState = MutableStateFlow<MyProgressUiState>(MyProgressUiState.Loading)
  val uiState: StateFlow<MyProgressUiState> = _uiState.asStateFlow()

  init {
    reload()
  }

  fun reload() {
    viewModelScope.launch {
      _uiState.update { MyProgressUiState.Loading }
      progressRepository.getMyProgress().fold(
        onSuccess = { progress -> _uiState.update { load(progress) } },
        onFailure = { error ->
          exceptionHandler.recordException(error)
          _uiState.update { MyProgressUiState.Error(retry = ::reload) }
        }
      )
    }
  }

  /** The player tapped Claim and the coins have landed. */
  fun claim(id: String) {
    val claimed = claimedMissionsStore.claim(id)
    _uiState.update { state ->
      if (state is MyProgressUiState.Idle) state.copy(claimed = claimed) else state
    }
  }

  // The progress is what the screen cannot do without. Everything else is best effort: a part
  // that fails to load is left out and the rest shows.
  private suspend fun load(progress: PaEUserProgress): MyProgressUiState.Idle = coroutineScope {
    val leaderboard = async {
      progressRepository.getWeeklyLeaderboard(LEADERBOARD_SIZE)
        .onFailure(exceptionHandler::recordException)
        .getOrNull()
    }
    val balance = async {
      runCatching {
        walletCoreDataSource.getCurrentWalletAddress()
          ?.let { walletInfoRepository.getWalletInfo(it).unitsBalance }
      }.getOrNull()
    }
    val games = installedGamesWithMissions()
      .map { packageName -> async { gameProgress(packageName, progress) } }
      .awaitAll()
      .sortedBy { it.name.lowercase() }

    MyProgressUiState.Idle(
      walletBalance = balance.await(),
      games = games,
      leaderboard = leaderboard.await(),
      lifetimePlaySeconds = progress.lifetimePlaySeconds,
      lifetimeUnits = progress.lifetimeUnits,
      claimed = claimedMissionsStore.claimed(),
    )
  }

  // The Play & Earn games are cached when the store lists them; a player who lands here first
  // has none yet, so they are fetched once.
  private suspend fun installedGamesWithMissions(): List<String> {
    var packages = campaignsRepository.getAvailablePackages().getOrNull().orEmpty()
    if (packages.isEmpty()) {
      campaignsRepository.getCampaigns()
      packages = campaignsRepository.getAvailablePackages().getOrNull().orEmpty()
    }
    return packages.filter(::isInstalled)
  }

  private fun isInstalled(packageName: String): Boolean =
    runCatching { context.packageManager.getPackageInfo(packageName, 0) }.isSuccess

  private suspend fun gameProgress(
    packageName: String,
    progress: PaEUserProgress,
  ): GameProgress {
    val app = campaignsRepository.getCachedApp(packageName).getOrNull()
    val missions = missionsRepository.getCampaignMissions(packageName, forceRefresh = true)
      .getOrNull()
      ?.missions
      .orEmpty()
    return GameProgress(
      packageName = packageName,
      name = app?.name?.takeIf { it.isNotBlank() } ?: packageName,
      icon = app?.icon?.takeIf { it.isNotBlank() },
      streak = gameWeekStreak(
        week = progress.streaks.find { it.packageName == packageName },
        streakMissions = progress.missions.filter { it.packageName == packageName },
        todayIndex = LocalDate.now().dayOfWeek.value - 1,
      ),
      missions = missions.mapNotNull { it.toCard(packageName) },
    )
  }
}

/** The screen's state and the two things the player can do on it: retry and claim. */
@Composable
fun rememberMyProgress(): Pair<MyProgressUiState, (String) -> Unit> = runPreviewable(
  preview = { MyProgressUiStateProvider().values.first() to {} },
  real = {
    val vm = hiltViewModel<MyProgressViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState to vm::claim
  }
)
