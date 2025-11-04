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
import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
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
  private val paECampaignsRepository: PaECampaignsRepository,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<PaEMissionsUiState>(PaEMissionsUiState.Loading)

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
    viewModelScope.launch {
      viewModelState.update { PaEMissionsUiState.Loading }

      paECampaignsRepository.observeCampaignMissions(packageName).collect { result ->
        result.fold(
          onSuccess = { missions ->
            viewModelState.update { PaEMissionsUiState.Idle(paeMissions = missions) }
          },
          onFailure = { throwable ->
            Timber.w(throwable)
            if (viewModelState.value is PaEMissionsUiState.Loading) {
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

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val paECampaignsRepository: PaECampaignsRepository
) : ViewModel()

@Composable
fun rememberPaEMissions(packageName: String): PaEMissionsUiState = runPreviewable(
  preview = { PaEMissionsUiStateProvider().values.toSet().random() },
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
            paECampaignsRepository = injectionsProvider.paECampaignsRepository
          ) as T
        }
      }
    )

    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
