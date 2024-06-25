package com.aptoide.android.aptoidegames.feature_payments.paypal

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import cm.aptoide.pt.extensions.ScreenData
import com.appcoins.payments.arch.emptyPurchaseRequest
import com.appcoins.payments.manager.presentation.rememberPaymentMethod
import com.appcoins.payments.methods.paypal.presentation.PaypalUIState
import com.appcoins.payments.methods.paypal.presentation.rememberPaypalUIState
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.drawables.icons.getCheck
import com.aptoide.android.aptoidegames.drawables.icons.getLogout
import com.aptoide.android.aptoidegames.feature_payments.AppGamesOtherPaymentMethodsButton
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
import com.aptoide.android.aptoidegames.feature_payments.presentation.PaypalPaymentStateEffect
import com.aptoide.android.aptoidegames.feature_payments.presentation.PreSelectedPaymentMethodViewModel
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.delay

private const val PAYPAL_PAYMENT_ID_ARG = "paymentMethodId"
private const val IS_PRE_SELECTED = "isPreSelected"
private const val PAYPAL_ROUTE = "payments/paypal"
private const val PAYPAL_FULL_ROUTE =
  "$PAYPAL_ROUTE?$PAYPAL_PAYMENT_ID_ARG={$PAYPAL_PAYMENT_ID_ARG}&$IS_PRE_SELECTED={$IS_PRE_SELECTED}"

private val paypalPaymentArguments = listOf(
  navArgument(PAYPAL_PAYMENT_ID_ARG) {
    type = NavType.StringType
    nullable = false
  },
  navArgument(IS_PRE_SELECTED) {
    type = NavType.BoolType
    defaultValue = false
    nullable = false
  }
)

fun buildPaypalRoute(
  paymentMethodId: String,
  isPreSelected: Boolean = false,
) =
  "$PAYPAL_ROUTE?$PAYPAL_PAYMENT_ID_ARG=${paymentMethodId}&$IS_PRE_SELECTED=${isPreSelected}"

fun paypalPaymentScreen(onFinish: (Boolean) -> Unit) = ScreenData.withAnalytics(
  route = PAYPAL_FULL_ROUTE,
  screenAnalyticsName = "PayPal",
  arguments = paypalPaymentArguments
) { args, _, popBackStack ->
  val paymentMethodId = args?.getString(PAYPAL_PAYMENT_ID_ARG)!!
  val isPreSelected = args.getBoolean(IS_PRE_SELECTED)
  BuildPaypalScreen(
    paymentMethodId = paymentMethodId,
    onFinish = onFinish,
    popBackStack = popBackStack,
  )

  BackHandler(
    enabled = isPreSelected,
    onBack = {
      onFinish(false)
    }
  )
}

@Composable
private fun BuildPaypalScreen(
  paymentMethodId: String,
  onFinish: (Boolean) -> Unit,
  popBackStack: () -> Unit,
) {
  val localContext = LocalContext.current
  val activityResultRegistry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
  val uiState = rememberPaypalUIState(paymentMethodId)
  var finished by remember { mutableStateOf(false) }

  val paymentMethod = rememberPaymentMethod(paymentMethodId)
  val genericAnalytics = rememberGenericAnalytics()

  PaypalPaymentStateEffect(paymentMethodId, uiState)

  LaunchedEffect(key1 = uiState, key2 = activityResultRegistry) {
    when (uiState) {
      PaypalUIState.Error -> {
        genericAnalytics.sendPaymentConclusionEvent(
          paymentMethod = paymentMethod,
          status = "error",
        )
      }

      is PaypalUIState.Success -> {
        genericAnalytics.sendPaymentConclusionEvent(
          paymentMethod = paymentMethod,
          status = "success",
        )
        delay(3000)
        if (!finished) onFinish(true)
        finished = true
      }

      PaypalUIState.Canceled -> popBackStack()
      is PaypalUIState.GetBillingAgreement -> uiState.resolveWith(activityResultRegistry)
      PaypalUIState.PaypalAgreementRemoved -> {
        genericAnalytics.sendPaymentBackEvent(paymentMethod = paymentMethod)
        popBackStack()
      }

      else -> {}
    }
  }

  PaypalScreen(
    viewModelState = uiState,
    onBuyClicked = {
      genericAnalytics.sendPaymentBuyEvent(paymentMethod)
    },
    onOtherPaymentMethodsClick = {
      genericAnalytics.sendPaymentBackEvent(paymentMethod = paymentMethod)
      popBackStack()
    },
    onClick = {
      if (uiState is PaypalUIState.Success) {
        onFinish(true)
        finished = true
      }
    },
    onOutsideClick = {
      genericAnalytics.sendPaymentDismissedEvent(
        paymentMethod = paymentMethod,
        context = uiState.paymentContext,
      )
      onFinish(uiState is PaypalUIState.Success)
      finished = true
    },
    onRetryClick = {
      genericAnalytics.sendPaymentTryAgainEvent(paymentMethod = paymentMethod)
      popBackStack()
    },
    onContactUs = {
      SupportActivity.openForSupport(localContext)
    }
  )
}

@Composable
private fun PaypalScreen(
  modifier: Modifier = Modifier,
  onBuyClicked: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
  onClick: () -> Unit,
  onOutsideClick: () -> Unit,
  onRetryClick: () -> Unit,
  viewModelState: PaypalUIState,
  onContactUs: () -> Unit,
) {
  AppGamesPaymentBottomSheet(
    modifier = modifier,
    onClick = onClick,
    onOutsideClick = onOutsideClick
  ) {
    val preSelectedPaymentMethodViewModel = hiltViewModel<PreSelectedPaymentMethodViewModel>()
    when (viewModelState) {
      PaypalUIState.Loading -> LoadingView()
      PaypalUIState.MakingPurchase -> LoadingView(
        textMessage = R.string.purchase_making_purchase_title
      )

      PaypalUIState.NoConnection -> PayPalNoConnectionScreen(onRetryClick)
      PaypalUIState.Error -> PaypalErrorScreen(onRetryClick, onContactUs)
      is PaypalUIState.Success -> SuccessView()
      PaypalUIState.Canceled -> LoadingView()
      is PaypalUIState.GetBillingAgreement -> LoadingView()

      is PaypalUIState.BillingAgreementAvailable -> BillingAgreementScreen(
        buyingPackage = viewModelState.purchaseRequest.domain,
        onBuyClick = {
          viewModelState.onBuyClick()
          onBuyClicked()
        },
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick,
        onRemoveBillingAgreementClick = {
          viewModelState.onRemoveBillingAgreementClick()
          preSelectedPaymentMethodViewModel.setSelection(null)
        },
        paymentMethodName = viewModelState.paymentMethodName,
        paymentMethodIconUrl = viewModelState.paymentMethodIconUrl,
      )

      PaypalUIState.PaypalAgreementRemoved -> LoadingView()
    }
  }
}

@Composable
private fun PaypalErrorScreen(
  onRetryClick: () -> Unit,
  onContactUs: () -> Unit,
) {
  when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> LandscapePaymentErrorView(
      onRetryClick = onRetryClick,
      onContactUsClick = onContactUs
    )

    else -> PortraitPaymentErrorView(
      onRetryClick = onRetryClick,
      onContactUsClick = onContactUs
    )
  }
}

@Composable
private fun PayPalNoConnectionScreen(onRetryClick: () -> Unit) {
  when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> LandscapePaymentsNoConnectionView(onRetryClick)
    else -> PortraitPaymentsNoConnectionView(onRetryClick)
  }
}

@Composable
private fun PaypalAccountScreen(
  modifier: Modifier = Modifier,
  paymentMethodName: String,
  paymentMethodIconUrl: String,
  onRemoveBillingAgreementClick: () -> Unit,
) {
  Column(
    modifier = modifier.padding(vertical = 8.dp),
    horizontalAlignment = Alignment.End
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Palette.GreyLight)
        .border(1.dp, Palette.Black)
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      AptoideAsyncImage(
        modifier = Modifier.size(22.dp),
        data = paymentMethodIconUrl,
        contentDescription = null
      )

      Text(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        text = paymentMethodName,
        maxLines = 2,
        style = AGTypography.DescriptionGames,
        overflow = TextOverflow.Ellipsis,
        color = Palette.Black
      )
      Image(
        imageVector = getCheck(Palette.Black),
        contentDescription = null,
      )
    }
    Row(
      modifier = Modifier
        .clickable(onClick = onRemoveBillingAgreementClick)
        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        modifier = Modifier.padding(end = 16.dp),
        text = "Logout", // TODO fix this string
        style = AGTypography.InputsM,
        color = Palette.Error
      )
      Icon(
        imageVector = getLogout(Palette.Error),
        contentDescription = null,
        tint = Palette.Error
      )
    }
  }
}

@Composable
private fun BillingAgreementScreen(
  modifier: Modifier = Modifier,
  buyingPackage: String,
  paymentMethodName: String,
  paymentMethodIconUrl: String,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
  onRemoveBillingAgreementClick: () -> Unit,
) {
  when (LocalConfiguration.current.orientation) {
    Configuration.ORIENTATION_LANDSCAPE -> {
      BillingAgreementScreenLandscape(
        modifier = modifier,
        buyingPackage = buyingPackage,
        onBuyClick = onBuyClick,
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick,
        onRemoveBillingAgreementClick = onRemoveBillingAgreementClick,
        paymentMethodName = paymentMethodName,
        paymentMethodIconUrl = paymentMethodIconUrl,
      )
    }

    else -> {
      BillingAgreementScreenPortrait(
        modifier = modifier,
        buyingPackage = buyingPackage,
        onBuyClick = onBuyClick,
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick,
        onRemoveBillingAgreementClick = onRemoveBillingAgreementClick,
        paymentMethodName = paymentMethodName,
        paymentMethodIconUrl = paymentMethodIconUrl,
      )
    }
  }
}

@Composable
private fun BillingAgreementScreenLandscape(
  modifier: Modifier,
  buyingPackage: String,
  paymentMethodName: String,
  paymentMethodIconUrl: String,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
  onRemoveBillingAgreementClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 8.dp)
  ) {
    Column(
      modifier = modifier
        .fillMaxSize()
        .weight(1f)
        .verticalScroll(rememberScrollState())
    ) {
      Row {
        PurchaseInfoRow(
          modifier = Modifier
            .fillMaxWidth(0.4f)
            .padding(end = 8.dp),
          buyingPackage = buyingPackage,
        )
        PaypalAccountScreen(
          paymentMethodName = paymentMethodName,
          paymentMethodIconUrl = paymentMethodIconUrl,
          onRemoveBillingAgreementClick = onRemoveBillingAgreementClick
        )
      }
      // hack to fill the remaining space. this makes the view scrollable in smaller views and in bigger screens, it forces the buttons to be on bottom
      Spacer(modifier = Modifier.weight(1f))
      AppGamesOtherPaymentMethodsButton(
        modifier = Modifier.padding(top = 8.dp),
        onOtherPaymentMethodsClick = onOtherPaymentMethodsClick
      )
    }
    PrimaryButton(
      modifier = Modifier.fillMaxWidth(),
      onClick = onBuyClick,
      title = "Buy", // TODO hardcoded string
    )
  }
}

@Composable
private fun BillingAgreementScreenPortrait(
  modifier: Modifier,
  buyingPackage: String,
  paymentMethodName: String,
  paymentMethodIconUrl: String,
  onBuyClick: () -> Unit,
  onOtherPaymentMethodsClick: () -> Unit,
  onRemoveBillingAgreementClick: () -> Unit,
) {
  Column(
    modifier = modifier
      .padding(horizontal = 16.dp)
      .padding(top = 16.dp, bottom = 8.dp),
    horizontalAlignment = Alignment.End
  ) {
    PurchaseInfoRow(buyingPackage = buyingPackage)
    PaypalAccountScreen(
      paymentMethodName = paymentMethodName,
      paymentMethodIconUrl = paymentMethodIconUrl,
      onRemoveBillingAgreementClick = onRemoveBillingAgreementClick
    )
    PaymentButtons(
      onBuyClick = onBuyClick,
      onOtherPaymentMethodsClick = onOtherPaymentMethodsClick
    )
  }
}

@PreviewDark
@Composable
private fun PaypalScreenPreview(
  @PreviewParameter(PaypalUIStateProvider::class) state: PaypalUIState,
) {
  AptoideTheme {
    PaypalScreen(
      viewModelState = state,
      onBuyClicked = {},
      onOtherPaymentMethodsClick = {},
      onClick = {},
      onOutsideClick = {},
      onRetryClick = {},
      onContactUs = {}
    )
  }
}

@PreviewLandscapeDark
@Composable
private fun PaypalScreenLandscapePreview(
  @PreviewParameter(PaypalUIStateProvider::class) state: PaypalUIState,
) {
  AptoideTheme {
    PaypalScreen(
      viewModelState = state,
      onBuyClicked = {},
      onOtherPaymentMethodsClick = {},
      onClick = {},
      onOutsideClick = {},
      onRetryClick = {},
      onContactUs = {}
    )
  }
}

private class PaypalUIStateProvider : PreviewParameterProvider<PaypalUIState> {
  override val values: Sequence<PaypalUIState> = sequenceOf(
    PaypalUIState.BillingAgreementAvailable(
      purchaseRequest = emptyPurchaseRequest,
      paymentMethodName = "Payment Method Name",
      paymentMethodIconUrl = "",
      onBuyClick = {},
      onRemoveBillingAgreementClick = {},
    ),
    PaypalUIState.MakingPurchase,
    PaypalUIState.Success,
    PaypalUIState.Loading,
    PaypalUIState.NoConnection,
    PaypalUIState.Error,
  )
}
