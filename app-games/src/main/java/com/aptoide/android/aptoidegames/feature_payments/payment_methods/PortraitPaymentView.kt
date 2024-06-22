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
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.PurchaseInfoRow
import com.aptoide.android.aptoidegames.feature_payments.presentation.rememberHasPreselectedPaymentMethod

@PreviewDark
@Composable
private fun PortraitPaymentViewPreview(
  @PreviewParameter(PaymentMethodsUiStateProvider::class) state: PaymentMethodsUiState,
) {
  MaterialTheme {
    PortraitPaymentView(
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
fun PortraitPaymentView(
  buyingPackage: String,
  paymentState: PaymentMethodsUiState,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  onWalletPaymentMethodClick: () -> Unit,
  onNetworkError: (() -> Unit)?,
  onContactUsClick: () -> Unit,
) {
  val hasPreselectedPaymentMethod = rememberHasPreselectedPaymentMethod()
  when (paymentState) {
    PaymentMethodsUiState.Error -> PortraitPaymentErrorView(
      onRetryClick = onNetworkError,
      onContactUsClick = onContactUsClick
    )

    PaymentMethodsUiState.NoConnection -> PortraitPaymentsNoConnectionView(onRetryClick = onNetworkError)

    is PaymentMethodsUiState.Loading -> if (hasPreselectedPaymentMethod) {
      LoadingView()
    } else {
      PortraitLoadingView(
        buyingPackage = buyingPackage,
        onWalletPaymentMethodClick = onWalletPaymentMethodClick
      )
    }

    is PaymentMethodsUiState.Idle -> PortraitPaymentsView(
      buyingPackage = buyingPackage,
      paymentMethods = paymentState.paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
      onWalletPaymentMethodClick = onWalletPaymentMethodClick
    )
  }
}

@Composable
private fun PortraitPaymentsView(
  buyingPackage: String,
  paymentMethods: List<PaymentMethod<*>>,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  onWalletPaymentMethodClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 16.dp),
  ) {
    PurchaseInfoRow(
      modifier = Modifier.padding(bottom = 16.dp),
      buyingPackage = buyingPackage
    )
    PaymentMethodsList(
      paymentMethods = paymentMethods,
      onPaymentMethodClick = onPaymentMethodClick,
      onWalletPaymentMethodClick = onWalletPaymentMethodClick,
    )
    Spacer(modifier = Modifier.height(16.dp))
  }
}

@Composable
private fun PortraitLoadingView(
  buyingPackage: String,
  onWalletPaymentMethodClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 16.dp),
  ) {
    PurchaseInfoRow(
      modifier = Modifier.padding(bottom = 16.dp),
      buyingPackage = buyingPackage,
    )
    PaymentMethodsListSkeleton(onWalletPaymentMethodClick = onWalletPaymentMethodClick)
    Spacer(modifier = Modifier.height(16.dp))
  }
}
