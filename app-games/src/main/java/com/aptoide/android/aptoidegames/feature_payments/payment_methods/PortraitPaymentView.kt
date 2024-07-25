package com.aptoide.android.aptoidegames.feature_payments.payment_methods

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.emptyPurchaseRequest
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.PurchaseInfoRow
import com.aptoide.android.aptoidegames.feature_payments.presentation.rememberHasPreselectedPaymentMethod
import java.io.IOException

@PreviewDark
@Composable
private fun PortraitPaymentViewPreview(
  @PreviewParameter(PaymentMethodsUiStateProvider::class) state: PaymentMethodsUiState,
) {
  MaterialTheme {
    PortraitPaymentView(
      purchaseRequest = emptyPurchaseRequest,
      paymentState = state,
      onPaymentMethodClick = {},
      onContactUsClick = {},
    )
  }
}

@Composable
fun PortraitPaymentView(
  purchaseRequest: PurchaseRequest,
  paymentState: PaymentMethodsUiState,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  onContactUsClick: () -> Unit,
) {
  val hasPreselectedPaymentMethod = rememberHasPreselectedPaymentMethod()
  when (paymentState) {
    is PaymentMethodsUiState.Error -> when (paymentState.error) {
      is IOException -> PortraitPaymentsNoConnectionView(
        onRetryClick = paymentState.reload
      )

      else -> PortraitPaymentErrorView(
        onRetryClick = paymentState.reload,
        onContactUsClick = onContactUsClick
      )
    }

    is PaymentMethodsUiState.Loading -> if (hasPreselectedPaymentMethod) {
      LoadingView()
    } else {
      PortraitLoadingView(
        purchaseRequest = purchaseRequest,
        onPaymentMethodClick = onPaymentMethodClick
      )
    }

    is PaymentMethodsUiState.Idle -> PortraitPaymentsView(
      purchaseRequest = purchaseRequest,
      paymentMethods = paymentState.paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
    )
  }
}

@Composable
private fun PortraitPaymentsView(
  purchaseRequest: PurchaseRequest,
  paymentMethods: List<PaymentMethod<*>>,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 16.dp),
  ) {
    PurchaseInfoRow(
      modifier = Modifier.padding(bottom = 16.dp),
      buyingPackage = purchaseRequest.domain
    )
    PaymentMethodsList(
      purchaseRequest = purchaseRequest,
      paymentMethods = paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
    )
    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun PortraitLoadingView(
  purchaseRequest: PurchaseRequest,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 16.dp),
  ) {
    PurchaseInfoRow(
      modifier = Modifier.padding(bottom = 16.dp),
      buyingPackage = purchaseRequest.domain,
    )
    PaymentMethodsListSkeleton(
      purchaseRequest = purchaseRequest,
      onPaymentMethodClick = onPaymentMethodClick
    )
    Spacer(modifier = Modifier.height(16.dp))
  }
}
