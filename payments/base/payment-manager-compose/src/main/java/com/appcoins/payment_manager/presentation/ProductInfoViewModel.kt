package com.appcoins.payment_manager.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcoins.payment_manager.di.PaymentsModule
import com.appcoins.payments.arch.PaymentManager
import com.appcoins.payments.arch.ProductInfoData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Composable
fun rememberProductInfo(): ProductInfoData? {
  val vm: ProductInfoViewModel = viewModel(
    key = "productInfoViewModel",
    factory = object : Factory {
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProductInfoViewModel(
          paymentManager = PaymentsModule.paymentManager
        ) as T
      }
    }
  )
  val uiState by vm.uiState.collectAsState()
  return uiState
}

class ProductInfoViewModel(
  private val paymentManager: PaymentManager,
) : ViewModel() {

  private val viewModelState =
    MutableStateFlow<ProductInfoData?>(null)

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
      paymentManager.productInfo.collect { productInfo ->
        viewModelState.update { productInfo }
      }
    }
  }
}
