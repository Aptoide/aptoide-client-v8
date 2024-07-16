package com.aptoide.android.aptoidegames.feature_payments.wallet

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.PreviewLandscapeDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.AppUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import cm.aptoide.pt.feature_home.domain.BundleSource
import com.appcoins.payments.arch.PaymentsResult
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.emptyPurchaseRequest
import com.appcoins.payments.uri_handler.PaymentsCancelledResult
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.drawables.icons.getAppcoinsClearLogo
import com.aptoide.android.aptoidegames.drawables.icons.getBonus
import com.aptoide.android.aptoidegames.feature_payments.AppGamesPaymentBottomSheet
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.LandscapePaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.LoadingView
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentErrorView
import com.aptoide.android.aptoidegames.feature_payments.PortraitPaymentsNoConnectionView
import com.aptoide.android.aptoidegames.feature_payments.PurchaseInfoRow
import com.aptoide.android.aptoidegames.feature_payments.analytics.PaymentContext
import com.aptoide.android.aptoidegames.installer.UserActionDialog
import com.aptoide.android.aptoidegames.installer.analytics.AnalyticsInstallPackageInfoMapper
import com.aptoide.android.aptoidegames.installer.forceInstallConstraints
import com.aptoide.android.aptoidegames.installer.notifications.rememberInstallerNotifications
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.installer.presentation.rememberSaveAppDetails
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

private const val paymentsWalletInstallationRoute = "paymentsWalletInstallation"

fun getPaymentsWalletInstallationRoute() = paymentsWalletInstallationRoute.withBundleMeta(
  BundleMeta(
    tag = "in_app_wallet",
    bundleSource = BundleSource.AUTOMATIC.name,
  )
)

fun paymentsWalletInstallationScreen(
  purchaseRequest: PurchaseRequest,
  onFinish: (PaymentsResult) -> Unit,
) = ScreenData.withAnalytics(
  route = paymentsWalletInstallationRoute,
  screenAnalyticsName = "PaymentsWalletInstallation"
) { _, navigate, navigateBack ->
  PaymentsWalletInstallationBottomSheetView(
    purchaseRequest = purchaseRequest,
    onFinish = onFinish,
    navigateBack = navigateBack,
    navigate = navigate,
  )
  UserActionDialog()
}

@PreviewDark
@Composable
fun PaymentsWalletInstallationBottomSheetViewPreview(
  @PreviewParameter(AppUiStateProvider::class) uiState: AppUiState,
) {
  PaymentsWalletInstallationView(
    purchaseRequest = emptyPurchaseRequest,
    uiState = uiState,
    navigateBack = {},
    navigate = {},
    onOutsideClick = {}
  )
}

@PreviewLandscapeDark
@Composable
fun PaymentsWalletInstallationBottomSheetViewLandscapePreview(
  @PreviewParameter(AppUiStateProvider::class) uiState: AppUiState,
) {
  PaymentsWalletInstallationView(
    purchaseRequest = emptyPurchaseRequest,
    uiState = uiState,
    navigateBack = {},
    navigate = {},
    onOutsideClick = {}
  )
}

@Composable
private fun PaymentsWalletInstallationBottomSheetView(
  purchaseRequest: PurchaseRequest,
  onFinish: (PaymentsResult) -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val genericAnalytics = rememberGenericAnalytics()
  val walletPaymentMethod = rememberWalletPaymentMethod(purchaseRequest)
  val (uiState, _) = rememberApp(packageName = "com.appcoins.wallet", adListId = "")

  PaymentsWalletInstallationView(
    purchaseRequest = purchaseRequest,
    uiState = uiState,
    navigateBack = navigateBack,
    navigate = navigate,
    onOutsideClick = {
      walletPaymentMethod?.let {
        genericAnalytics.sendPaymentDismissedEvent(
          paymentMethod = it,
          context = PaymentContext.SECOND_STEP,
        )
      }
      onFinish(PaymentsCancelledResult)
    }
  )
}

@Composable
fun PaymentsWalletInstallationView(
  purchaseRequest: PurchaseRequest,
  uiState: AppUiState,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
  onOutsideClick: () -> Unit,
) {
  val configuration = LocalConfiguration.current
  val analyticsContext = AnalyticsContext.current
  val genericAnalytics = rememberGenericAnalytics()

  AppGamesPaymentBottomSheet(
    onOutsideClick = onOutsideClick
  ) {
    when (uiState) {
      is AppUiState.Idle -> {
        val walletApp = uiState.app
        val (_, saveAppDetailsBlocking) = rememberSaveAppDetails()
        val downloadState = rememberDownloadState(app = walletApp)
        var installStarted by remember { mutableStateOf(false) }
        val installerNotifications = rememberInstallerNotifications()

        LaunchedEffect(key1 = downloadState) {
          when {
            downloadState is DownloadUiState.Install && !installStarted -> {
              installStarted = true
              saveAppDetailsBlocking(walletApp)
              AnalyticsInstallPackageInfoMapper.currentAnalyticsUIContext = analyticsContext
              downloadState.installWith { _, resolve ->
                resolve(forceInstallConstraints)
              }
              installerNotifications.onInstallationQueued(walletApp.packageName)
            }

            downloadState is DownloadUiState.Installed -> navigate(paymentsWalletInstalledRoute)
            else -> Unit
          }
        }

        when (configuration.orientation) {
          Configuration.ORIENTATION_LANDSCAPE -> WalletInstallationLandscapeView(
            walletApp = walletApp,
            buyingPackage = purchaseRequest.domain,
            onWalletInstallStarted = {
              genericAnalytics.sendAppCoinsInstallStarted(
                packageName = walletApp.packageName,
                analyticsContext = analyticsContext
              )
            },
            onWalletInstallationCanceled = navigateBack
          )

          else -> WalletInstallationPortraitView(
            walletApp = walletApp,
            buyingPackage = purchaseRequest.domain,
            onWalletInstallStarted = {
              genericAnalytics.sendAppCoinsInstallStarted(
                packageName = walletApp.packageName,
                analyticsContext = analyticsContext
              )
            },
            onWalletInstallationCanceled = navigateBack
          )
        }
      }

      is AppUiState.NoConnection -> when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE ->
          LandscapePaymentsNoConnectionView(onRetryClick = navigateBack)

        else -> PortraitPaymentsNoConnectionView(onRetryClick = navigateBack)
      }

      is AppUiState.Error -> when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> LandscapePaymentErrorView(
          onRetryClick = navigateBack,
          onContactUsClick = {}
        )

        else -> PortraitPaymentErrorView(
          onRetryClick = navigateBack,
          onContactUsClick = {}
        )
      }

      is AppUiState.Loading -> LoadingView()
    }
  }
}

@Composable
fun WalletInstallationPortraitView(
  walletApp: App,
  buyingPackage: String,
  onWalletInstallStarted: () -> Unit,
  onWalletInstallationCanceled: () -> Unit,
) {
  Column(
    modifier = Modifier.padding(all = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Image(
      modifier = Modifier.fillMaxWidth(),
      imageVector = getBonus(Palette.Black, Palette.Primary),
      contentDescription = null
    )
    PurchaseInfoRow(
      modifier = Modifier.padding(top = 32.dp),
      buyingPackage = buyingPackage
    )
    Divider(
      modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
      color = Palette.GreyLight
    )
    PayWithWalletInstallationCard(
      walletApp = walletApp,
      onWalletInstallStarted = onWalletInstallStarted,
      onWalletInstallationCanceled = onWalletInstallationCanceled
    )
  }
}

@Composable
fun WalletInstallationLandscapeView(
  walletApp: App,
  buyingPackage: String,
  onWalletInstallStarted: () -> Unit,
  onWalletInstallationCanceled: () -> Unit,
) {
  Row(
    modifier = Modifier
      .wrapContentHeight()
      .fillMaxWidth()
      .padding(all = 16.dp)
      .verticalScroll(rememberScrollState()),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(0.4f),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Image(
        imageVector = getBonus(Palette.Black, Palette.Primary),
        contentDescription = null
      )
      PurchaseInfoRow(
        modifier = Modifier.padding(top = 8.dp),
        buyingPackage = buyingPackage
      )
    }
    PayWithWalletInstallationCard(
      walletApp = walletApp,
      modifier = Modifier.padding(start = 8.dp),
      onWalletInstallStarted = onWalletInstallStarted,
      onWalletInstallationCanceled = onWalletInstallationCanceled
    )
  }
}

@Composable
fun PayWithWalletInstallationCard(
  walletApp: App,
  modifier: Modifier = Modifier,
  onWalletInstallStarted: () -> Unit = {},
  onWalletInstallationCanceled: () -> Unit = {},
) {
  Column(
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        imageVector = getAppcoinsClearLogo(Palette.AppCoinsPink),
        contentDescription = null,
      )
      Text(
        modifier = Modifier.padding(start = 8.dp),
        text = "Pay with the AppCoins Wallet", //TODO hardcoded string
        style = AGTypography.InputsM,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = Palette.Black
      )
    }
    Text(
      text = "You'll get a bonus in all purchases!", //TODO hardcoded string
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      style = AGTypography.Body,
      color = Palette.Black
    )
    AppInstallationViewSmall(
      app = walletApp,
      modifier = Modifier.padding(top = 16.dp),
      onInstallStarted = onWalletInstallStarted,
      onCancel = onWalletInstallationCanceled,
    )
  }
}

@Composable
fun AppInstallationViewSmall(
  app: App,
  modifier: Modifier = Modifier,
  onInstallStarted: () -> Unit = {},
  onCancel: () -> Unit = {},
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    AppIconWProgress(
      app = app,
      modifier = Modifier
        .size(64.dp),
      contentDescription = null,
    )
    Column(
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp)
        .weight(1f),
      verticalArrangement = Arrangement.SpaceEvenly
    ) {
      Text(
        modifier = Modifier.padding(bottom = 8.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        text = app.name,
        style = AGTypography.DescriptionGames,
        color = Palette.Black
      )
      ProgressText(
        app = app,
        showVersionName = false
      )
    }
    InstallViewShort(
      app = app,
      onInstallStarted = onInstallStarted,
      onCancel = onCancel,
      cancelable = true
    )
  }
}
