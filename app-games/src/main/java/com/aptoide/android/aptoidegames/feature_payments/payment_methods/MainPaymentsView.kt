package com.aptoide.android.aptoidegames.feature_payments.payment_methods

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import cm.aptoide.pt.extensions.ScreenData
import com.appcoins.payments.arch.PaymentMethod
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.emptyPaymentMethod
import com.appcoins.payments.arch.emptyPurchaseRequest
import com.appcoins.payments.manager.presentation.PaymentMethodsUiState
import com.appcoins.payments.manager.presentation.rememberPaymentMethods
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.SupportActivity
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.getAppcoinsClearLogo
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.drawables.icons.getTintedWalletGift
import com.aptoide.android.aptoidegames.feature_payments.AppGamesPaymentBottomSheet
import com.aptoide.android.aptoidegames.feature_payments.currentProductInfo
import com.aptoide.android.aptoidegames.feature_payments.getRoute
import com.aptoide.android.aptoidegames.feature_payments.presentation.PreselectedPaymentMethodEffect
import com.aptoide.android.aptoidegames.feature_payments.wallet.WalletPaymentMethod
import com.aptoide.android.aptoidegames.feature_payments.wallet.rememberWalletPaymentMethod
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import java.io.IOException

const val paymentsRoute = "payments"

fun paymentsScreen(
  onFinish: (Boolean) -> Unit,
  purchaseRequest: PurchaseRequest,
) = ScreenData.withAnalytics(
  route = paymentsRoute,
  screenAnalyticsName = "PaymentMethods"
) { _, navigate, _ ->
  MainPaymentsView(
    navigate = navigate,
    onFinish = onFinish,
    purchaseRequest = purchaseRequest
  )
}

@Composable
private fun MainPaymentsView(
  navigate: (String) -> Unit,
  onFinish: (Boolean) -> Unit,
  purchaseRequest: PurchaseRequest,
) {
  val context = LocalContext.current
  val onContactUsClick = { SupportActivity.openForSupport(context) }
  val (paymentState, reload) = rememberPaymentMethods(purchaseRequest = purchaseRequest)

  PreselectedPaymentMethodEffect(paymentState, navigate)

  val productInfo = currentProductInfo()
  val genericAnalytics = rememberGenericAnalytics()

  var hasPaymentStartBeenSent by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(key1 = productInfo, key2 = paymentState, key3 = hasPaymentStartBeenSent) {
    productInfo?.let {
      if (paymentState is PaymentMethodsUiState.Idle && !hasPaymentStartBeenSent) {
        genericAnalytics.sendPaymentStartEvent(
          packageName = purchaseRequest.domain,
          productInfoData = it,
        )
        hasPaymentStartBeenSent = true
      }
    }
  }

  ShowPaymentsList(
    purchaseRequest = purchaseRequest,
    paymentState = paymentState,
    onOutsideClick = {
      genericAnalytics.sendPaymentMethodsDismissedEvent(
        packageName = purchaseRequest.domain,
        productInfoData = productInfo,
      )
      onFinish(false)
    },
    onPaymentMethodClick = { paymentMethod ->
      genericAnalytics.sendPaymentMethodsEvent(paymentMethod = paymentMethod)
      navigate(paymentMethod.getRoute())
    },
    onNetworkError = reload,
    onContactUsClick = onContactUsClick
  )
}

@Composable
private fun ShowPaymentsList(
  purchaseRequest: PurchaseRequest,
  paymentState: PaymentMethodsUiState,
  onOutsideClick: () -> Unit,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  onNetworkError: (() -> Unit)?,
  onContactUsClick: () -> Unit,
) {
  val configuration = LocalConfiguration.current
  AppGamesPaymentBottomSheet(
    onOutsideClick = onOutsideClick,
  ) {
    when (configuration.orientation) {
      Configuration.ORIENTATION_LANDSCAPE -> {
        LandscapePaymentView(
          purchaseRequest = purchaseRequest,
          paymentState = paymentState,
          onPaymentMethodClick = onPaymentMethodClick,
          onNetworkError = onNetworkError,
          onContactUsClick = onContactUsClick,
        )
      }

      else -> {
        PortraitPaymentView(
          purchaseRequest = purchaseRequest,
          paymentState = paymentState,
          onPaymentMethodClick = onPaymentMethodClick,
          onNetworkError = onNetworkError,
          onContactUsClick = onContactUsClick,
        )
      }
    }
  }
}

@Composable
fun PaymentMethodsList(
  purchaseRequest: PurchaseRequest,
  paymentMethods: List<PaymentMethod<*>>,
  onPaymentMethodClick: (PaymentMethod<*>) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(paymentMethods) {
      PaymentMethodView(
        iconUrl = it.iconUrl,
        label = it.label,
        onPaymentMethodClick = { onPaymentMethodClick(it) },
      )
    }
    item {
      WalletPaymentMethod(
        purchaseRequest = purchaseRequest,
        onPaymentMethodClick = onPaymentMethodClick
      )
    }
  }
}

@Composable
private fun PaymentMethodView(
  modifier: Modifier = Modifier,
  iconUrl: String,
  label: String,
  onPaymentMethodClick: () -> Unit,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(Palette.GreyLight)
      .padding(16.dp)
      .clickable(onClick = onPaymentMethodClick),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    AptoideAsyncImage(
      modifier = Modifier.size(22.dp),
      data = iconUrl,
      contentDescription = null
    )
    Text(
      text = label,
      maxLines = 2,
      style = AGTypography.InputsM,
      color = Palette.Black,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .weight(1f)
    )
    Image(
      imageVector = getLeftArrow(Palette.Primary, Palette.Black),
      contentDescription = null,
      modifier = Modifier
        .size(24.dp)
        .rotate(180f)
    )
  }
}

@Composable
private fun WalletPaymentMethod(
  purchaseRequest: PurchaseRequest,
  onPaymentMethodClick: (WalletPaymentMethod) -> Unit,
) {
  val walletPaymentMethod = rememberWalletPaymentMethod(purchaseRequest = purchaseRequest)
  walletPaymentMethod ?: return
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = { onPaymentMethodClick(walletPaymentMethod) }),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Palette.GreyLight)
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Image(
        modifier = Modifier.size(22.dp),
        imageVector = getAppcoinsClearLogo(Palette.Black),
        contentDescription = null
      )
      Column(
        modifier = Modifier
          .padding(horizontal = 16.dp)
          .weight(1f)
      ) {
        Text(
          text = "Pay with the AppCoins Wallet", // TODO hardcoded string
          maxLines = 1,
          style = AGTypography.InputsM,
          color = Palette.Black,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "It'll be installed automatically and you'll get a bonus in all purchases!", // TODO hardcoded string
          maxLines = 2,
          style = AGTypography.Body,
          overflow = TextOverflow.Ellipsis,
          color = Palette.Black,
          modifier = Modifier.padding(end = 10.dp)
        )
      }
      Image(
        imageVector = getLeftArrow(Palette.Primary, Palette.Black),
        contentDescription = null,
        modifier = Modifier
          .size(24.dp)
          .rotate(180f)
      )
    }
    Row(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()
        .background(Palette.Secondary)
        .padding(vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
    ) {
      Image(
        imageVector = getTintedWalletGift(
          iconColor = Palette.Primary,
          outlineColor = Palette.Black
        ),
        contentDescription = null,
      )
      AptoideOutlinedText(
        text = "Up to 20% Bonus", // TODO hardcoded string
        style = AGTypography.InputsS,
        outlineWidth = 15f,
        outlineColor = Palette.Black,
        textColor = Palette.Primary,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
      Image(
        imageVector = getTintedWalletGift(
          iconColor = Palette.Primary,
          outlineColor = Palette.Black
        ),
        contentDescription = null,
      )
    }
  }
}

@Composable
fun PaymentMethodsListSkeleton(
  purchaseRequest: PurchaseRequest,
  onPaymentMethodClick: (WalletPaymentMethod) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    PaymentMethodSkeleton()
    WalletPaymentMethod(
      purchaseRequest = purchaseRequest,
      onPaymentMethodClick = onPaymentMethodClick
    )
  }
}

@Composable
private fun PaymentMethodSkeleton() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(56.dp)
      .background(Palette.GreyLight)
      .padding(bottom = 8.dp)
  )
}

@Composable
fun LoadingView() {
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 360.dp)
  ) {
    CircularProgressIndicator(
      modifier = Modifier
        .padding(bottom = 16.dp)
        .size(64.dp, 64.dp),
      backgroundColor = Color.Transparent,
      color = Palette.Primary,
      strokeWidth = 8.dp
    )
  }
}

@PreviewDark
@Composable
private fun ShowPaymentsListNoConnectionPreviewPortrait(
  @PreviewParameter(PaymentMethodsUiStateProvider::class) state: PaymentMethodsUiState,
) {
  AptoideTheme {
    ShowPaymentsList(
      purchaseRequest = emptyPurchaseRequest,
      paymentState = state,
      onOutsideClick = {},
      onPaymentMethodClick = {},
      onNetworkError = {},
      onContactUsClick = {},
    )
  }
}

@PreviewLandscapeDark
@Composable
private fun ShowPaymentsListNoConnectionPreviewLandscape(
  @PreviewParameter(PaymentMethodsUiStateProvider::class) state: PaymentMethodsUiState,
) {
  AptoideTheme {
    ShowPaymentsList(
      purchaseRequest = emptyPurchaseRequest,
      paymentState = state,
      onOutsideClick = {},
      onPaymentMethodClick = {},
      onNetworkError = {},
      onContactUsClick = {},
    )
  }
}

class PaymentMethodsUiStateProvider : PreviewParameterProvider<PaymentMethodsUiState> {
  override val values: Sequence<PaymentMethodsUiState> = sequenceOf(
    PaymentMethodsUiState.Idle(listOf(emptyPaymentMethod, emptyPaymentMethod)),
    PaymentMethodsUiState.Loading,
    PaymentMethodsUiState.Error(Exception()),
    PaymentMethodsUiState.Error(IOException()),
  )
}
