package cm.aptoide.pt.campaigns.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.campaigns.data.PaEMissionsRepository
import cm.aptoide.pt.campaigns.domain.PaEMission
import cm.aptoide.pt.campaigns.domain.PaEMissionStatus
import cm.aptoide.pt.campaigns.domain.PaEMissions
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class PaEAppMissionsViewModel(
  private val packageName: String,
  private val paEMissionsRepository: PaEMissionsRepository,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<PaEMissionsUiState>(PaEMissionsUiState.Loading)
  private var reloadJob: Job? = null

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    reload()
  }

  fun reload() {
    reloadJob?.cancel()
    reloadJob = viewModelScope.launch {
      // Only show Loading if no data has been loaded yet to avoid layout shifts
      if (viewModelState.value !is PaEMissionsUiState.Idle) {
        viewModelState.update { PaEMissionsUiState.Loading }
      }

      paEMissionsRepository.observeCampaignMissions(packageName).collect { result ->
        result.fold(
          onSuccess = { missions ->
            viewModelState.update {
              PaEMissionsUiState.Idle(paeMissions = missions.sortedByStatus())
            }
          },
          onFailure = { throwable ->
            Timber.w(throwable)
            if (viewModelState.value !is PaEMissionsUiState.Idle) {
              viewModelState.update {
                when (throwable) {
                  is IOException -> PaEMissionsUiState.NoConnection
                  else -> PaEMissionsUiState.Error
                }
              }
            }
          }
        )
      }
    }
  }
}

/** Sorts checkpoints and missions: IN_PROGRESS first, then PENDING, then COMPLETED. */
private fun PaEMissions.sortedByStatus() = PaEMissions(
  checkpoints = checkpoints.sortedByStatus(),
  missions = missions.sortedByStatus()
)

private fun List<PaEMission>.sortedByStatus() = sortedBy { mission ->
  when (mission.progress?.status) {
    PaEMissionStatus.IN_PROGRESS -> 0
    PaEMissionStatus.PENDING -> 1
    PaEMissionStatus.COMPLETED -> 2
    null -> 1
  }
}

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val paEMissionsRepository: PaEMissionsRepository
) : ViewModel()

@Composable
fun rememberPaEMissions(packageName: String): Pair<PaEMissionsUiState, () -> Unit> = runPreviewable(
  preview = { PaEMissionsUiStateProvider().values.first() to {} },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: PaEAppMissionsViewModel = viewModel(
      viewModelStoreOwner = LocalContext.current as ViewModelStoreOwner,
      key = "paeMissions/$packageName",
      factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return PaEAppMissionsViewModel(
            packageName = packageName,
            paEMissionsRepository = injectionsProvider.paEMissionsRepository
          ) as T
        }
      }
    )

    val uiState by vm.uiState.collectAsState()
    uiState to vm::reload
  }
)
