package com.appcoins.payment_manager.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.payment_manager.manager.PaymentManager
import com.appcoins.payment_manager.repository.product.domain.ProductInfoData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductInfoViewModel @Inject constructor(
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
