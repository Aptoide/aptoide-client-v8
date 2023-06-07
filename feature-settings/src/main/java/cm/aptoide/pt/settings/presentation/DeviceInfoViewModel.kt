package cm.aptoide.pt.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.settings.data.DeviceInfoRepository
import cm.aptoide.pt.settings.domain.DeviceInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceInfoViewModel @Inject constructor(
  private val deviceInfoRepository: DeviceInfoRepository,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(DeviceInfo())

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      viewModelState.update {
        deviceInfoRepository.getDeviceInfo()
      }
    }
  }
}
