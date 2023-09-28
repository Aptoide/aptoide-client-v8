package cm.aptoide.pt.payment_manager.presentation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.payment_manager.manager.PaymentManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val paymentManager: PaymentManager,
) : ViewModel()

@Composable
fun paymentViewModel(uri: Uri?): PaymentViewModel {
  val injectionsProvider = hiltViewModel<InjectionsProvider>()
  return viewModel(
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PaymentViewModel(
          uri = uri,
          paymentManager = injectionsProvider.paymentManager,
        ) as T
      }
    }
  )
}
