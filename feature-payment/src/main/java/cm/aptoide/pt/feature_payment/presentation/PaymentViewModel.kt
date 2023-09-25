package cm.aptoide.pt.feature_payment.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_payment.manager.PaymentManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PaymentViewModel(
  private val uri: Uri?,
  private val paymentManager: PaymentManager,
) : ViewModel() {

  private val viewModelState = MutableStateFlow("") // TODO change in future tickets

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

}
