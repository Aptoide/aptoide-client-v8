package cm.aptoide.pt.app_games.network.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.app_games.network.repository.NetworkPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkPreferencesViewModel @Inject constructor(
  private val networkPreferencesManager: NetworkPreferencesRepository,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(true)

  val downloadOnlyOverWifi = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      networkPreferencesManager.shouldDownloadOnlyOverWifi()
        .catch { throwable -> throwable.printStackTrace() }
        .collect { downloadOnlyOverWifi ->
          viewModelState.update { downloadOnlyOverWifi }
        }
    }
  }

  fun setDownloadOnlyOverWifi(downloadOnlyOverWifi: Boolean) {
    viewModelScope.launch {
      networkPreferencesManager.setDownloadOnlyOverWifi(downloadOnlyOverWifi)
    }
  }
}
