package com.aptoide.android.aptoidegames.feature_payments.wallet

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import cm.aptoide.pt.extensions.ScreenData
import com.appcoins.payments.arch.PaymentsResult
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.uri_handler.PaymentsActivityResult
import com.appcoins.payments.uri_handler.PaymentsCancelledResult
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.drawables.icons.getWalletInstalled
import com.aptoide.android.aptoidegames.feature_payments.AppGamesPaymentBottomSheet
import com.aptoide.android.aptoidegames.feature_payments.analytics.PaymentContext
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.delay

const val paymentsWalletInstalledRoute = "paymentsWalletInstalled"

fun paymentsWalletInstalledScreen(
  purchaseRequest: PurchaseRequest,
  onFinish: (PaymentsResult) -> Unit,
) = ScreenData.withAnalytics(
  route = paymentsWalletInstalledRoute,
  screenAnalyticsName = "WalletInstallSuccess"
) { _, _, _ ->
  PaymentsWalletInstalledView(
    purchaseRequest = purchaseRequest,
    onFinish = onFinish
  )
}

@PreviewDark
@Composable
fun PaymentsWalletInstalledViewPreview() {
  WalletInstalledView(
    onOutsideClick = {},
    onClick = {}
  )
}

@PreviewLandscapeDark
@Composable
fun PaymentsWalletInstalledViewLandscapePreview() {
  WalletInstalledView(
    onOutsideClick = {},
    onClick = {}
  )
}

@Composable
fun PaymentsWalletInstalledView(
  purchaseRequest: PurchaseRequest,
  onFinish: (PaymentsResult) -> Unit,
) {
  val genericAnalytics = rememberGenericAnalytics()
  val walletPaymentMethod = rememberWalletPaymentMethod(purchaseRequest)
  val launcher = rememberLauncherForActivityResult(
    contract = StartActivityForResult()
  ) {
    onFinish(PaymentsActivityResult(it.resultCode, it.data))
  }

  val onRedirect: () -> Unit = {
    purchaseRequest.uri?.let {
      val intent = Intent(Intent.ACTION_VIEW).setPackage("com.appcoins.wallet")
      intent.data = it

      try {
        launcher.launch(intent)
      } catch (e: Exception) {
        onFinish(PaymentsCancelledResult)
      }
    }
  }

  var launched by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    delay(3000)
    if (!launched) onRedirect()
    launched = true
  }

  WalletInstalledView(
    onOutsideClick = {
      walletPaymentMethod?.let {
        genericAnalytics.sendPaymentDismissedEvent(
          paymentMethod = it,
          context = PaymentContext.CONCLUSION,
        )
      }
      onFinish(PaymentsCancelledResult)
    },
    onClick = {
      launched = true
      onRedirect()
    }
  )
}

@Composable
fun WalletInstalledView(
  onOutsideClick: () -> Unit,
  onClick: () -> Unit,
) {
  AppGamesPaymentBottomSheet(
    onClick = onClick,
    onOutsideClick = onOutsideClick
  ) {
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
      Configuration.ORIENTATION_LANDSCAPE -> WalletInstalledLandscapeView()
      else -> WalletInstalledPortraitView()
    }
  }
}

@Composable
fun WalletInstalledPortraitView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
      modifier = Modifier.padding(top = 40.dp, start = 16.dp, end = 16.dp),
      imageVector = getWalletInstalled(
        color1 = Palette.AppCoinsPink,
        color2 = Palette.White,
        color3 = Palette.Primary,
        color4 = Palette.Black
      ),
      contentScale = ContentScale.FillBounds,
      contentDescription = null
    )
    Text(
      modifier = Modifier.padding(top = 54.dp, start = 32.dp, end = 32.dp),
      text = stringResource(id = R.string.iap_appc_wallet_install_success_body),
      style = AGTypography.Title,
      textAlign = TextAlign.Center,
      color = Palette.Black
    )
    Text(
      modifier = Modifier.padding(top = 12.dp, bottom = 88.dp, start = 32.dp, end = 32.dp),
      text = stringResource(id = R.string.iap_appc_wallet_install_success_title),
      style = AGTypography.DescriptionGames,
      textAlign = TextAlign.Center,
      color = Palette.Black
    )
  }
}

@Composable
fun WalletInstalledLandscapeView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
      modifier = Modifier.padding(top = 40.dp),
      imageVector = getWalletInstalled(
        color1 = Palette.AppCoinsPink,
        color2 = Palette.White,
        color3 = Palette.Primary,
        color4 = Palette.Black
      ),
      contentScale = ContentScale.FillBounds,
      contentDescription = null
    )
    Text(
      modifier = Modifier.padding(top = 16.dp, start = 10.dp, end = 10.dp),
      text = stringResource(id = R.string.iap_appc_wallet_install_success_body),
      style = AGTypography.Title,
      textAlign = TextAlign.Center,
      color = Palette.Black
    )
    Text(
      modifier = Modifier.padding(top = 6.dp, start = 10.dp, end = 10.dp, bottom = 40.dp),
      text = stringResource(id = R.string.iap_appc_wallet_install_success_title),
      style = AGTypography.DescriptionGames,
      textAlign = TextAlign.Center,
      color = Palette.Black
    )
  }
}
