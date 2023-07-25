package cm.aptoide.pt.installer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.installer.platform.UserActionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserActionViewModel @Inject constructor(
  private val handler: UserActionHandler,
) : ViewModel() {

  val uiState = handler.requests
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      null
    )

  fun onResult(result: Boolean) = handler.onResult(result)
}
