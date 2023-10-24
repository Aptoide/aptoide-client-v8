package cm.aptoide.pt.payment_manager.presentation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val paymentManager: PaymentManager,
) : ViewModel()

@Composable
fun paymentMethodsViewModel(purchaseRequest: PurchaseRequest?): PaymentMethodsViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentMethodsViewModel(
          purchaseRequest = purchaseRequest,
          paymentManager = injectionsProvider.paymentManager,
        ) as T
      }
    }
  )
}
