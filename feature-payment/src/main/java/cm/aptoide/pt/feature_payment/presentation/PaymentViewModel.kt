package cm.aptoide.pt.feature_payment.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_payment.manager.PaymentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
  private val paymentManager: PaymentManager
) : ViewModel() {

  private val viewModelState = MutableStateFlow(0) // TODO change in future tickets

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )
}
