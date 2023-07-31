package cm.aptoide.pt.appcoins.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.appcoins.repository.GamificationRepository
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BonusViewModel @Inject constructor(
  private val gamificationRepository: GamificationRepository,
) : ViewModel() {
  private val viewModelState = MutableStateFlow<Float?>(null)

  val uiState: StateFlow<Float?> = viewModelState.stateIn(
    viewModelScope,
    SharingStarted.Eagerly,
    viewModelState.value
  )

  init {
    viewModelScope.launch {
      try {
        val result = gamificationRepository.getGamification().bonusPercentage
        viewModelState.update { result }
      } catch (t: Throwable) {
        Timber.e(t)
      }
    }
  }
}

@Composable
fun rememberBonus(): Float? = runPreviewable(
  preview = { 15f },
  real = {
    val vm = hiltViewModel<BonusViewModel>()
    val bonus by vm.uiState.collectAsState()
    bonus
  }
)
