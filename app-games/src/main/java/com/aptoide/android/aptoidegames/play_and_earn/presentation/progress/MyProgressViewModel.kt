package com.aptoide.android.aptoidegames.play_and_earn.presentation.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.campaigns.data.PaEProgressRepository
import cm.aptoide.pt.campaigns.domain.GetCachedPaEAppUseCase
import cm.aptoide.pt.campaigns.domain.PaEGameStreak
import cm.aptoide.pt.exception_handler.ExceptionHandler
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LEADERBOARD_SIZE = 10

@HiltViewModel
class MyProgressViewModel @Inject constructor(
  private val progressRepository: PaEProgressRepository,
  private val getCachedPaEApp: GetCachedPaEAppUseCase,
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
        onSuccess = { progress ->
          // The leaderboard is the least of the screen: without it the rest still shows.
          val leaderboard = progressRepository.getWeeklyLeaderboard(LEADERBOARD_SIZE)
            .onFailure(exceptionHandler::recordException)
            .getOrNull()
          val streaks = progress.streaks.map { it.withAppInfo() }
          _uiState.update {
            MyProgressUiState.Idle(
              lifetimePlaySeconds = progress.lifetimePlaySeconds,
              lifetimeUnits = progress.lifetimeUnits,
              missions = progress.missions,
              streaks = streaks,
              leaderboard = leaderboard,
            )
          }
        },
        onFailure = { error ->
          exceptionHandler.recordException(error)
          _uiState.update { MyProgressUiState.Error(retry = ::reload) }
        }
      )
    }
  }

  // The backend only knows the package; the name and icon come from the Play & Earn games this
  // device has already listed. An unknown game still shows, by its package.
  private suspend fun PaEGameStreak.withAppInfo(): GameStreak {
    val app = getCachedPaEApp(packageName).getOrNull()
    return GameStreak(
      packageName = packageName,
      name = app?.name?.takeIf { it.isNotBlank() } ?: packageName,
      icon = app?.icon?.takeIf { it.isNotBlank() },
      days = days,
      daysPlayed = daysPlayed,
    )
  }
}

@Composable
fun rememberMyProgressUiState(): MyProgressUiState = runPreviewable(
  preview = { MyProgressUiStateProvider().values.first() },
  real = {
    val vm = hiltViewModel<MyProgressViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
