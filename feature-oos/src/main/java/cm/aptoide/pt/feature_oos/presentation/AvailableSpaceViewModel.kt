package cm.aptoide.pt.feature_oos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_oos.domain.AvailableSpaceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AvailableSpaceViewModel constructor(
  private val availableSpaceUseCase: AvailableSpaceUseCase,
  private val appSize: Long,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(
    0L
  )

  val availableSpaceState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      availableSpaceUseCase.getRequiredSpace(appSize)
        .catch { throwable -> throwable.printStackTrace() }
        .collect { requiredSpace ->
          viewModelState.update { requiredSpace }
        }
    }
  }
}
