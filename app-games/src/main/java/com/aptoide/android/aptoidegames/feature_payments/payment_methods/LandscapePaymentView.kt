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
import com.appcoins.payments.arch.PaymentMethod
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
      buyingPackage = "Buying Package",
      paymentState = state,
      onPaymentMethodClick = {},
      onWalletPaymentMethodClick = {},
      onNetworkError = {},
      onContactUsClick = {},
    )
  }
}

@Composable
fun LandscapePaymentView(
  buyingPackage: String,
  paymentState: PaymentMethodsUiState,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  onWalletPaymentMethodClick: () -> Unit,
  onNetworkError: (() -> Unit)?,
  onContactUsClick: () -> Unit,
) {
  val hasPreselectedPaymentMethod = rememberHasPreselectedPaymentMethod()
  when (paymentState) {
    PaymentMethodsUiState.Error -> LandscapePaymentErrorView(
      onRetryClick = onNetworkError,
      onContactUsClick = onContactUsClick
    )

    PaymentMethodsUiState.NoConnection -> LandscapePaymentsNoConnectionView(onRetryClick = onNetworkError)

    is PaymentMethodsUiState.Loading -> if (hasPreselectedPaymentMethod) {
      LoadingView()
    } else {
      LandscapeLoadingView(
        buyingPackage = buyingPackage,
        onWalletPaymentMethodClick = onWalletPaymentMethodClick
      )
    }

    is PaymentMethodsUiState.Idle -> LandscapePaymentsView(
      buyingPackage = buyingPackage,
      paymentMethods = paymentState.paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
      onWalletPaymentMethodClick = onWalletPaymentMethodClick
    )
  }
}

@Composable
private fun LandscapeLoadingView(
  buyingPackage: String,
  onWalletPaymentMethodClick: () -> Unit,
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
        buyingPackage = buyingPackage,
      )
    }
    PaymentMethodsListSkeleton(
      onWalletPaymentMethodClick = onWalletPaymentMethodClick,
      modifier = Modifier
        .fillMaxSize()
        .weight(0.5f),
    )
  }
}

@Composable
private fun LandscapePaymentsView(
  buyingPackage: String,
  paymentMethods: List<PaymentMethod<*>>,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  onWalletPaymentMethodClick: () -> Unit,
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
        buyingPackage = buyingPackage
      )
    }
    PaymentMethodsList(
      paymentMethods = paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
      onWalletPaymentMethodClick = onWalletPaymentMethodClick,
      modifier = Modifier
        .fillMaxSize()
        .weight(0.5f)
    )
  }
}
