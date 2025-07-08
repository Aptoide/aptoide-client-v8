package cm.aptoide.pt.campaigns.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class PaEBundlesViewModel @Inject constructor(
  private val paECampaignsRepository: PaECampaignsRepository,
) : ViewModel() {

  private val viewModelState = MutableStateFlow<PaEBundlesUiState>(PaEBundlesUiState.Loading)

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
      viewModelState.update { PaEBundlesUiState.Loading }
      try {
        val result = paECampaignsRepository.getCampaigns().getOrThrow()
        viewModelState.update {
          PaEBundlesUiState.Idle(bundles = result)
        }
      } catch (t: Throwable) {
        Timber.w(t)
        viewModelState.update {
          when (t) {
            is IOException -> PaEBundlesUiState.NoConnection
            else -> PaEBundlesUiState.Error
          }
        }
      }
    }
  }
}

@Composable
fun rememberPaEBundles(): PaEBundlesUiState = runPreviewable(
  preview = { PaEBundlesUiStateProvider().values.toSet().random() },
  real = {
    val vm = hiltViewModel<PaEBundlesViewModel>()
    val uiState by vm.uiState.collectAsState()
    uiState
  }
)
