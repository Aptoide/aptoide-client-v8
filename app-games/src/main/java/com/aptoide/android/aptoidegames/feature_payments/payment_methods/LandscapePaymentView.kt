package com.aptoide.android.aptoidegames.feature_payments.payment_methods

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import com.appcoins.payments.arch.ConnectionFailedException
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.emptyPurchaseRequest
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.PurchaseInfoRow
import com.aptoide.android.aptoidegames.feature_payments.presentation.rememberHasPreselectedPaymentMethod
import com.aptoide.android.aptoidegames.theme.AptoideTheme

@PreviewLandscapeDark
@Composable
private fun LandscapePaymentViewPreview(
  @PreviewParameter(PaymentMethodsUiStateProvider::class) state: PaymentMethodsUiState,
) {
  AptoideTheme {
    LandscapePaymentView(
      purchaseRequest = emptyPurchaseRequest,
      paymentState = state,
      onPaymentMethodClick = {},
      onContactUsClick = {},
    )
  }
}

@Composable
fun LandscapePaymentView(
  purchaseRequest: PurchaseRequest,
  paymentState: PaymentMethodsUiState,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  onContactUsClick: () -> Unit,
) {
  val hasPreselectedPaymentMethod = rememberHasPreselectedPaymentMethod()
  when (paymentState) {
    is PaymentMethodsUiState.Error -> when (paymentState.error) {
      is ConnectionFailedException -> LandscapePaymentsNoConnectionView(
        onRetryClick = paymentState.reload
      )

      else -> LandscapePaymentErrorView(
        onRetryClick = paymentState.reload,
        onContactUsClick = onContactUsClick
      )
    }

    is PaymentMethodsUiState.Loading -> if (hasPreselectedPaymentMethod) {
      LoadingView()
    } else {
      LandscapeLoadingView(
        purchaseRequest = purchaseRequest,
        onPaymentMethodClick = onPaymentMethodClick
      )
    }

    is PaymentMethodsUiState.Idle -> LandscapePaymentsView(
      purchaseRequest = purchaseRequest,
      paymentMethods = paymentState.paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
    )
  }
}

@Composable
private fun LandscapeLoadingView(
  purchaseRequest: PurchaseRequest,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 24.dp)
      .padding(top = 16.dp),
    verticalAlignment = Alignment.Top,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxSize()
        .weight(0.5f)
        .padding(end = 16.dp)
    ) {
      PurchaseInfoRow(
        modifier = Modifier.padding(bottom = 8.dp),
        buyingPackage = purchaseRequest.domain,
      )
    }
    PaymentMethodsListSkeleton(
      purchaseRequest = purchaseRequest,
      onPaymentMethodClick = onPaymentMethodClick,
      modifier = Modifier
        .fillMaxSize()
        .weight(0.5f),
    )
  }
}

@Composable
private fun LandscapePaymentsView(
  purchaseRequest: PurchaseRequest,
  paymentMethods: List<PaymentMethod<*>>,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 24.dp)
      .padding(top = 16.dp),
    verticalAlignment = Alignment.Top,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Column(
      verticalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .fillMaxSize()
        .weight(0.5f)
    ) {
      PurchaseInfoRow(
        modifier = Modifier.padding(bottom = 8.dp),
        buyingPackage = purchaseRequest.domain
      )
    }
    PaymentMethodsList(
      purchaseRequest = purchaseRequest,
      paymentMethods = paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
      modifier = Modifier
        .fillMaxSize()
        .weight(0.5f)
    )
  }
}
