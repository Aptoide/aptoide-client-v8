package com.aptoide.android.aptoidegames.feature_payments.credit_card

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewLandscapeLight
import cm.aptoide.pt.extensions.PreviewLight
import cm.aptoide.pt.extensions.ScreenData
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardView
import com.appcoins.payments.arch.ConnectionFailedException
import com.appcoins.payments.arch.PaymentsResult
import com.appcoins.payments.manager.presentation.rememberPaymentMethod
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState
import com.appcoins.payments.methods.adyen.presentation.rememberAdyenCreditCardUIState
import com.appcoins.payments.uri_handler.PaymentsCancelledResult
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.feature_payments.AppGamesPaymentBottomSheet
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.LoadingView
import com.aptoide.android.aptoidegames.feature_payments.PaymentButtons
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.PurchaseInfoRow
import com.aptoide.android.aptoidegames.feature_payments.SuccessView
import com.aptoide.android.aptoidegames.feature_payments.analytics.paymentContext
import com.aptoide.android.aptoidegames.feature_payments.getAdyenErrorDescription
import com.aptoide.android.aptoidegames.feature_payments.getAdyenErrorMessage
import com.aptoide.android.aptoidegames.feature_payments.presentation.AdyenCreditCardStateEffect
import com.aptoide.android.aptoidegames.feature_payments.presentation.PreSelectedPaymentMethodViewModel
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val CREDIT_CARD_PAYMENT_ID_ARG = "paymentMethodId"
private const val IS_PRE_SELECTED = "isPreSelected"
private const val CREDIT_CARD_ROUTE = "payments/creditCard"
private const val CREDIT_CARD_FULL_ROUTE =
  "$CREDIT_CARD_ROUTE?$CREDIT_CARD_PAYMENT_ID_ARG={$CREDIT_CARD_PAYMENT_ID_ARG}&$IS_PRE_SELECTED={$IS_PRE_SELECTED}"

private val creditCardPaymentArguments = listOf(
  navArgument(CREDIT_CARD_PAYMENT_ID_ARG) {
    type = NavType.StringType
    nullable = false
  },
  navArgument(IS_PRE_SELECTED) {
    type = NavType.BoolType
    defaultValue = false
    nullable = false
  }
)

fun buildCreditCardRoute(
  paymentMethodId: String,
  isPreSelected: Boolean = false,
) =
  "$CREDIT_CARD_ROUTE?$CREDIT_CARD_PAYMENT_ID_ARG=${paymentMethodId}&$IS_PRE_SELECTED=${isPreSelected}"

fun creditCardPaymentScreen(
  onFinish: (PaymentsResult) -> Unit,
) = ScreenData.withAnalytics(
  route = CREDIT_CARD_FULL_ROUTE,
  screenAnalyticsName = "CreditCard",
  arguments = creditCardPaymentArguments
) { args, _, popBackStack ->
  val paymentMethodId = args?.getString(CREDIT_CARD_PAYMENT_ID_ARG)!!
  val isPreSelected = args.getBoolean(IS_PRE_SELECTED)
  BuildAdyenCreditCardScreen(
    paymentMethodId = paymentMethodId,
    onFinish = onFinish,
    popBackStack = popBackStack,
  )

  BackHandler(
    enabled = isPreSelected,
    onBack = { onFinish(PaymentsCancelledResult) }
  )
}

@Composable
private fun BuildAdyenCreditCardScreen(
  paymentMethodId: String,
  onFinish: (PaymentsResult) -> Unit,
  popBackStack: () -> Unit,
) {
  val context = LocalContext.current as ComponentActivity
  val activityResultRegistry =
    LocalActivityResultRegistryOwner.current!!.activityResultRegistry
  val lifecycleOwner = LocalLifecycleOwner.current
  val uiState = rememberAdyenCreditCardUIState()
  val preSelectedPaymentMethodViewModel = hiltViewModel<PreSelectedPaymentMethodViewModel>()
  var onBuyClick by remember { mutableStateOf<(() -> Unit)?>(null) }

  var finished by remember { mutableStateOf(false) }

  val paymentMethod = rememberPaymentMethod(paymentMethodId)
  val genericAnalytics = rememberGenericAnalytics()

  AdyenCreditCardStateEffect(paymentMethodId, uiState)

  LaunchedEffect(key1 = uiState, key2 = activityResultRegistry) {
    when (uiState) {
      is AdyenCreditCardUiState.Error -> {
        when (uiState.result) {
          is ConnectionFailedException -> genericAnalytics.sendPaymentConclusionEvent(
            paymentMethod = paymentMethod,
            status = "error",
            errorCode = "No network",
          )

          else -> genericAnalytics.sendPaymentConclusionEvent(
            paymentMethod = paymentMethod,
            status = "error",
            errorCode = uiState.result.message,
          )
        }
      }

      is AdyenCreditCardUiState.Input -> uiState.cardComponent(context)
        .observe(lifecycleOwner) { cardState ->
          onBuyClick = if (cardState.isReady && cardState.isInputValid) {
            {
              genericAnalytics.sendPaymentBuyEvent(paymentMethod)
              uiState.buy(cardState)
            }
          } else {
            null
          }
        }

      is AdyenCreditCardUiState.Success -> {
        genericAnalytics.sendPaymentConclusionEvent(
          paymentMethod = paymentMethod,
          status = "success",
        )
        delay(3000)
        if (!finished) onFinish(uiState.result)
        finished = true
      }

      is AdyenCreditCardUiState.UserAction -> uiState.resolveWith(activityResultRegistry)
      else -> {}
    }
  }

  AppGamesPaymentBottomSheet(
    onClick = {
      if (uiState is AdyenCreditCardUiState.Success) {
        onFinish(uiState.result)
        finished = true
      }
    },
    onOutsideClick = {
      onFinish((uiState as? AdyenCreditCardUiState.Success)?.result ?: PaymentsCancelledResult)
      genericAnalytics.sendPaymentDismissedEvent(
        paymentMethod = paymentMethod,
        context = uiState.paymentContext,
      )
      finished = true
    }
  ) {
    when (uiState) {
      is AdyenCreditCardUiState.MakingPurchase -> LoadingView(
        textMessage = R.string.purchase_making_purchase_title
      )

      is AdyenCreditCardUiState.Loading -> LoadingView()
      is AdyenCreditCardUiState.Error -> AdyenCreditCardErrorScreen(
        error = uiState.result,
        onRetryClick = {
          genericAnalytics.sendPaymentTryAgainEvent(paymentMethod = paymentMethod)
          popBackStack()
        },
        onContactUs = { SupportActivity.openForSupport(context) }
      )

      is AdyenCreditCardUiState.Input -> AdyenCreditCardScreen(
        packageName = uiState.purchaseRequest.domain,
        onBuyClickEnabled = onBuyClick != null,
        onBuyClick = onBuyClick ?: {},
        onOtherPaymentMethodsClick = {
          genericAnalytics.sendPaymentBackEvent(paymentMethod = paymentMethod)
          popBackStack()
        }
      ) {
        Column {
          AdyenCreditCardView(cardComponent = uiState.cardComponent(context))
          uiState.forgetCard?.let { forgetCard ->
            TextButton(
              onClick = {
                forgetCard()
                preSelectedPaymentMethodViewModel.setSelection(null)
              },
              modifier = Modifier
                .padding(end = 10.dp)
                .height(48.dp)
                .align(Alignment.End)
            ) {
              Text(
                text = stringResource(R.string.iab_change_card_button),
                style = AGTypography.InputsM,
                color = Palette.Black,
                textDecoration = TextDecoration.Underline
              )
            }
          }
        }
      }

      is AdyenCreditCardUiState.Success -> SuccessView()
      is AdyenCreditCardUiState.UserAction -> LoadingView(
        textMessage = R.string.purchase_making_purchase_title
      )
    }
  }
}

@Composable
private fun AdyenCreditCardErrorScreen(
  error: Throwable,
  onRetryClick: () -> Unit,
  onContactUs: () -> Unit,
) {
  val configuration = LocalConfiguration.current

  if (error is ConnectionFailedException) {
    when (configuration.orientation) {
      Configuration.ORIENTATION_LANDSCAPE -> LandscapePaymentsNoConnectionView(
        onRetryClick = onRetryClick
      )

      else -> PortraitPaymentsNoConnectionView(onRetryClick = onRetryClick)
    }
  } else {
    val message = getAdyenErrorMessage(error)?.let { stringResource(it) }
    val description = getAdyenErrorDescription(error)?.let { stringResource(it) }

    when (configuration.orientation) {
      Configuration.ORIENTATION_LANDSCAPE -> LandscapePaymentErrorView(
        message = message,
        description = description,
        onRetryClick = onRetryClick,
        onContactUsClick = onContactUs,
      )

      else -> PortraitPaymentErrorView(
        message = message,
        description = description,
        onRetryClick = onRetryClick,
        onContactUsClick = onContactUs,
      )
    }
  }
}

@Composable
private fun AdyenCreditCardScreen(
  modifier: Modifier = Modifier,
  packageName: String,
  onBuyClickEnabled: Boolean,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
  paymentView: @Composable () -> Unit,
) {
  val configuration = LocalConfiguration.current
  when (configuration.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> {
      AdyenCreditCardScreenLandscape(
        packageName = packageName,
        onBuyClickEnabled = onBuyClickEnabled,
        onBuyClick = onBuyClick,
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick,
        modifier = modifier,
        paymentView = paymentView
      )
    }

    else -> {
      AdyenCreditCardScreenPortrait(
        packageName = packageName,
        onBuyClickEnabled = onBuyClickEnabled,
        onBuyClick = onBuyClick,
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick,
        modifier = modifier,
        paymentView = paymentView
      )
    }
  }
}

@Composable
private fun AdyenCreditCardScreenLandscape(
  packageName: String,
  onBuyClickEnabled: Boolean,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
  modifier: Modifier = Modifier,
  paymentView: @Composable () -> Unit,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 8.dp)
  ) {
    Row {
      PurchaseInfoRow(
        modifier = Modifier.fillMaxWidth(0.4f),
        buyingPackage = packageName,
      )
      paymentView()
    }
    PaymentButtons(
      modifier = Modifier.padding(top = 16.dp),
      onBuyClickEnabled = onBuyClickEnabled,
      onBuyClick = onBuyClick,
      onOtherPaymentMethodsClick = onOtherPaymentMethodsClick
    )
  }
}

@Composable
private fun AdyenCreditCardScreenPortrait(
  packageName: String,
  onBuyClickEnabled: Boolean,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
  modifier: Modifier = Modifier,
  paymentView: @Composable () -> Unit,
) {
  Column(
    modifier = modifier
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 8.dp),
  ) {
    PurchaseInfoRow(buyingPackage = packageName)
    paymentView()
    PaymentButtons(
      modifier = Modifier.padding(top = 16.dp),
      onBuyClickEnabled = onBuyClickEnabled,
      onBuyClick = onBuyClick,
      onOtherPaymentMethodsClick = onOtherPaymentMethodsClick
    )
  }
}

@Composable
private fun AdyenCreditCardView(
  cardComponent: CardComponent,
  modifier: Modifier = Modifier,
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  AndroidView(
    factory = {
      CardView(it).apply {
        attach(cardComponent, lifecycleOwner)
      }
    },
    update = {
      it.findViewById<SwitchCompat>(com.adyen.checkout.card.R.id.switch_storePaymentMethod)?.run {
        if (isVisible) {
          isChecked = true
        }
      }
    },
    modifier = modifier.fillMaxWidth(),
  )
}

@PreviewLight
@Composable
private fun AdyenCreditCardScreenPortraitPreview() {
  AptoideTheme {
    AppGamesPaymentBottomSheet {
      AdyenCreditCardScreen(
        packageName = "packageName",
        onBuyClick = {},
        onOtherPaymentMethodsClick = {},
        onBuyClickEnabled = Random.nextBoolean(),
        paymentView = {
          Box(
            modifier = Modifier
              .padding(top = 16.dp)
              .height(150.dp)
              .fillMaxWidth()
              .border(1.dp, Palette.Black),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "Adyen View")
          }
        }
      )
    }
  }
}

@PreviewLandscapeLight
@Composable
private fun AdyenCreditCardScreenLandscapePreview() {
  AptoideTheme {
    AppGamesPaymentBottomSheet {
      AdyenCreditCardScreen(
        packageName = "packageName",
        onBuyClick = {},
        onOtherPaymentMethodsClick = {},
        onBuyClickEnabled = Random.nextBoolean(),
        paymentView = {
          Box(
            modifier = Modifier
              .padding(top = 16.dp)
              .height(150.dp)
              .fillMaxWidth()
              .border(1.dp, Palette.Black),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "Adyen View")
          }
        }
      )
    }
  }
}
