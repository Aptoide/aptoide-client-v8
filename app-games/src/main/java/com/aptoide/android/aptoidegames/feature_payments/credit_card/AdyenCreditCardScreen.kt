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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewLandscapeLight
import cm.aptoide.pt.extensions.PreviewLight
import cm.aptoide.pt.extensions.ScreenData
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardView
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState.Error
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState.Input
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState.Loading
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState.MakingPurchase
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState.Success
import com.appcoins.payments.methods.adyen.presentation.AdyenCreditCardUiState.UserAction
import com.appcoins.payments.methods.adyen.presentation.rememberAdyenCreditCardUIState
import com.appcoins.payments.methods.adyen.repository.NoNetworkException
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
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
import com.aptoide.android.aptoidegames.feature_payments.getAdyenErrorDescription
import com.aptoide.android.aptoidegames.feature_payments.getAdyenErrorMessage
import com.aptoide.android.aptoidegames.feature_payments.presentation.AdyenCreditCardStateEffect
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
  onFinish: (Boolean) -> Unit,
) = ScreenData.withAnalytics(
  route = CREDIT_CARD_FULL_ROUTE,
  screenAnalyticsName = "CreditCard",
  arguments = creditCardPaymentArguments
) { args, _, popBackStack ->
  val paymentMethodId = args?.getString(CREDIT_CARD_PAYMENT_ID_ARG)!!
  val isPreSelected = args.getBoolean(IS_PRE_SELECTED)
  BuildAdyenCreditCardScreen(
    onFinish = onFinish,
    popBackStack = popBackStack,
    paymentMethodId = paymentMethodId,
  )

  BackHandler(enabled = isPreSelected) {
    onFinish(false)
  }
}

@Composable
private fun BuildAdyenCreditCardScreen(
  onFinish: (Boolean) -> Unit,
  popBackStack: () -> Unit,
  paymentMethodId: String,
) {
  val context = LocalContext.current as ComponentActivity
  val lifecycleOwner = LocalLifecycleOwner.current
  val (uiState, buy) = rememberAdyenCreditCardUIState(paymentMethodId)
  var onBuyClick by remember { mutableStateOf<(() -> Unit)?>(null) }

  var finished by remember { mutableStateOf(false) }

  AdyenCreditCardStateEffect(paymentMethodId, uiState)

  AppGamesPaymentBottomSheet(
    onClick = {
      if (uiState is Success) {
        onFinish(true)
        finished = true
      }
    },
    onOutsideClick = {
      onFinish(uiState is Success)
      finished = true
    }
  ) {
    when (uiState) {
      is MakingPurchase -> LoadingView(textMessage = R.string.purchase_making_purchase_title)

      is Loading -> LoadingView()
      is Error -> AdyenCreditCardErrorScreen(
        error = uiState.error,
        onRetryClick = popBackStack,
        onContactUs = { SupportActivity.openForSupport(context) }
      )

      is Input -> {
        LaunchedEffect(Unit) {
          uiState.cardComponent(context).observe(lifecycleOwner) { cardState ->
            onBuyClick = if (cardState.isReady && cardState.isInputValid) {
              {
                buy(cardState)
              }
            } else {
              null
            }
          }
        }

        AdyenCreditCardScreen(
          packageName = uiState.purchaseRequest.domain,
          onBuyClickEnabled = onBuyClick != null,
          onBuyClick = onBuyClick ?: {},
          onOtherPaymentMethodsClick = { popBackStack() }
        ) {
          Column {
            AdyenCreditCardView(cardComponent = uiState.cardComponent(context))
            uiState.forgetCard?.let {
              TextButton(
                onClick = it,
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
      }

      is Success -> {
        LaunchedEffect(Unit) {
          delay(3000)
          if (!finished) onFinish(true)
          finished = true
        }
        SuccessView()
      }

      is UserAction -> {
        val activityResultRegistry =
          LocalActivityResultRegistryOwner.current!!.activityResultRegistry
        LoadingView(textMessage = R.string.purchase_making_purchase_title)
        LaunchedEffect(Unit) {
          uiState.resolveWith(activityResultRegistry)
        }
      }
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

  if (error is NoNetworkException) {
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
        modifier = modifier,
        packageName = packageName,
        onBuyClickEnabled = onBuyClickEnabled,
        onBuyClick = onBuyClick,
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick,
        paymentView = paymentView
      )
    }

    else -> {
      AdyenCreditCardScreenPortrait(
        modifier = modifier,
        packageName = packageName,
        onBuyClickEnabled = onBuyClickEnabled,
        onBuyClick = onBuyClick,
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick,
        paymentView = paymentView
      )
    }
  }
}

@Composable
private fun AdyenCreditCardScreenLandscape(
  modifier: Modifier = Modifier,
  packageName: String,
  onBuyClickEnabled: Boolean,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
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
  modifier: Modifier = Modifier,
  packageName: String,
  onBuyClickEnabled: Boolean,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
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
  modifier: Modifier = Modifier,
  cardComponent: CardComponent,
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
