package com.aptoide.android.aptoidegames.feature_payments.transaction

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import cm.aptoide.pt.extensions.ScreenData
import com.appcoins.payments.arch.ConnectionFailedException
import com.appcoins.payments.arch.PaymentsResult
import com.appcoins.payments.arch.PaymentsSuccessResult
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.UnknownErrorException
import com.appcoins.payments.manager.presentation.TransactionUIState
import com.appcoins.payments.manager.presentation.rememberOngoingTransactionUIState
import com.appcoins.payments.uri_handler.PaymentsCancelledResult
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.feature_payments.AppGamesPaymentBottomSheet
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.LoadingView
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.SuccessView
import com.aptoide.android.aptoidegames.feature_payments.analytics.paymentContext
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import kotlinx.coroutines.delay

private const val UID_ARG = "uid"
private const val TRANSACTION_ROUTE = "payments/transaction"
private const val TRANSACTION_FULL_ROUTE = "$TRANSACTION_ROUTE?$UID_ARG={$UID_ARG}"

fun buildOngoingTransactionRoute(uid: String) = "$TRANSACTION_ROUTE?$UID_ARG=${uid}"

fun ongoingTransactionScreen(
  purchaseRequest: PurchaseRequest,
  onFinish: (PaymentsResult) -> Unit,
) = ScreenData.withAnalytics(
  route = TRANSACTION_FULL_ROUTE,
  screenAnalyticsName = "Transaction",
  arguments = listOf(
    navArgument(UID_ARG) {
      type = NavType.StringType
      nullable = false
    },
    navArgument(UID_ARG) {
      type = NavType.StringType
      nullable = false
    },
  )
) { args, _, _ ->
  val uid = args?.getString(UID_ARG)!!
  BuildOngoingTransactionScreen(
    uid = uid,
    purchaseRequest = purchaseRequest,
    onFinish = onFinish,
  )

  BackHandler {
    onFinish(PaymentsCancelledResult)
  }
}

@Composable
private fun BuildOngoingTransactionScreen(
  uid: String,
  purchaseRequest: PurchaseRequest,
  onFinish: (PaymentsResult) -> Unit,
) {
  val localContext = LocalContext.current
  val (transaction, uiState) = rememberOngoingTransactionUIState(
    uid = uid,
    purchaseRequest = purchaseRequest
  )
  var finished by remember { mutableStateOf(false) }

  val genericAnalytics = rememberGenericAnalytics()

  LaunchedEffect(key1 = uiState) {
    when (uiState) {
      is TransactionUIState.Error -> {
        genericAnalytics.sendPaymentErrorEvent(
          transaction = transaction,
          errorCode = uiState.result.message
        )
      }

      is TransactionUIState.Success -> {
        genericAnalytics.sendPaymentSuccessEvent(transaction = transaction)
        delay(3000)
        if (!finished) onFinish(uiState.result)
        finished = true
      }

      else -> {}
    }
  }

  OngoingTransactionScreen(
    viewModelState = uiState,
    onClick = {
      if (uiState is TransactionUIState.Success) {
        onFinish(uiState.result)
        finished = true
      }
    },
    onOutsideClick = {
      genericAnalytics.sendPaymentDismissedEvent(
        transaction = transaction,
        context = uiState.paymentContext,
      )
      onFinish((uiState as? TransactionUIState.Success)?.result ?: PaymentsCancelledResult)
      finished = true
    },
    onContactUs = {
      SupportActivity.openForSupport(localContext)
    }
  )
}

@Composable
private fun OngoingTransactionScreen(
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
  onOutsideClick: () -> Unit,
  viewModelState: TransactionUIState,
  onContactUs: () -> Unit,
) {
  AppGamesPaymentBottomSheet(
    modifier = modifier,
    onClick = onClick,
    onOutsideClick = onOutsideClick
  ) {
    when (viewModelState) {
      TransactionUIState.Processing -> LoadingView(
        textMessage = R.string.purchase_making_purchase_title
      )

      is TransactionUIState.Error -> when (viewModelState.result) {
        is ConnectionFailedException -> TransactionNoConnectionScreen(viewModelState.reload)
        else -> TransactionErrorScreen(onContactUs)
      }

      is TransactionUIState.Success -> SuccessView()
    }
  }
}

@Composable
private fun TransactionNoConnectionScreen(onRetryClick: () -> Unit) {
  when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> LandscapePaymentsNoConnectionView(onRetryClick)
    else -> PortraitPaymentsNoConnectionView(onRetryClick)
  }
}

@Composable
private fun TransactionErrorScreen(
  onContactUs: () -> Unit,
) {
  when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> LandscapePaymentErrorView(
      onRetryClick = null,
      onContactUsClick = onContactUs
    )

    else -> PortraitPaymentErrorView(
      onRetryClick = null,
      onContactUsClick = onContactUs
    )
  }
}

@PreviewDark
@Composable
private fun OngoingPaymentScreenPreview(
  @PreviewParameter(TransactionUIStateProvider::class) state: TransactionUIState,
) {
  AptoideTheme {
    OngoingTransactionScreen(
      viewModelState = state,
      onClick = {},
      onOutsideClick = {},
      onContactUs = {}
    )
  }
}

@PreviewLandscapeDark
@Composable
private fun OngoingPaymentScreenLandscapePreview(
  @PreviewParameter(TransactionUIStateProvider::class) state: TransactionUIState,
) {
  AptoideTheme {
    OngoingTransactionScreen(
      viewModelState = state,
      onClick = {},
      onOutsideClick = {},
      onContactUs = {}
    )
  }
}

private class TransactionUIStateProvider : PreviewParameterProvider<TransactionUIState> {
  override val values: Sequence<TransactionUIState> = sequenceOf(
    TransactionUIState.Processing,
    TransactionUIState.Success(PaymentsSuccessResult()),
    TransactionUIState.Error(ConnectionFailedException()),
    TransactionUIState.Error(UnknownErrorException()),
  )
}
