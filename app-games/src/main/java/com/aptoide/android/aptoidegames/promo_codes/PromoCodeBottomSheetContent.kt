package com.aptoide.android.aptoidegames.promo_codes

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getPackageInfo
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.walletApp
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import cm.aptoide.pt.feature_apps.presentation.rememberWalletApp
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.BottomSheetHeader
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.design_system.TertiarySmallButton
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.drawables.icons.getBonusTextIcon
import com.aptoide.android.aptoidegames.error_views.BottomSheetGenericErrorView
import com.aptoide.android.aptoidegames.error_views.BottomSheetNoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.promo_codes.analytics.rememberPromoCodeAnalytics
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

class PromoCodeBottomSheet(
  private val promoCodeApp: PromoCodeApp,
  private val showSnack: (String) -> Unit,
) : BottomSheetContent {
  @Composable override fun Draw(
    dismiss: () -> Unit,
    navigate: (String) -> Unit,
  ) {
    val (walletAppUiState, walletReload) = rememberWalletApp()

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp)) {
      BottomSheetHeader()
      when (walletAppUiState) {
        is AppUiState.Idle -> {
          PromoCodeBottomSheetContent(
            promoCodeApp = promoCodeApp,
            walletApp = walletAppUiState.app,
            showSnack = showSnack
          )
        }

        is AppUiState.Loading -> LoadingView()
        is AppUiState.Error -> BottomSheetGenericErrorView(onRetryClick = walletReload)
        is AppUiState.NoConnection -> BottomSheetNoConnectionView(onRetryClick = walletReload)
      }
    }
  }
}

@Composable
fun PromoCodeBottomSheetContent(
  promoCodeApp: PromoCodeApp,
  walletApp: App,
  showSnack: (String) -> Unit
) {
  val context = LocalContext.current
  val packageInfo = remember { context.packageManager.getPackageInfo(promoCodeApp.packageName) }

  val promoCodeAnalytics = rememberPromoCodeAnalytics()

  LaunchedEffect(packageInfo) {
    if (packageInfo == null) {
      promoCodeAnalytics.sendPromoCodeImpressionEvent(status = "app_not_installed")
    }
  }

  val (appState, reload) = rememberApp(promoCodeApp.asSource())

  when (appState) {
    is AppUiState.Idle -> {
      val walletDisclaimer = stringResource(id = R.string.promo_code_install_wallet_disclaimer)
      val walletDownloadState = rememberDownloadState(app = walletApp)
      val showWalletSegment = remember { walletDownloadState !is DownloadUiState.Installed }
      val isWalletInstalled = walletDownloadState?.isPackageInstalled() == true

      val appDownloadState = rememberDownloadState(app = appState.app)

      LaunchedEffect(Unit) {
        promoCodeAnalytics.sendPromoCodeImpressionEvent(
          status = "success",
          withWallet = isWalletInstalled
        )
      }

      if (showWalletSegment) {
        WalletInstallSection(walletApp = walletApp)
      }

      Text(
        modifier = Modifier.padding(top = 4.dp),
        text = stringResource(
          id = R.string.promo_code_extra_title,
          "10" //TODO Hardcoded value (should come from backend in the future)
        ),
        color = Palette.White,
        style = AGTypography.Title
      )
      if (appDownloadState?.isPackageInstalled() == true) {
        AppItem(
          app = appState.app,
          onClick = {},
        ) {
          if (!isWalletInstalled) {
            TertiarySmallButton(
              onClick = {
                showSnack(walletDisclaimer)
                promoCodeAnalytics.sendPromoCodeClickEvent(withWallet = false)
              },
              title = stringResource(id = R.string.promo_code_use_button)
            )
          } else {
            PrimarySmallButton(
              onClick = {
                val uri = Uri.parse("appcoins://promocode?promocode=${promoCodeApp.promoCode}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
                promoCodeAnalytics.sendPromoCodeClickEvent(withWallet = true)
              },
              title = stringResource(id = R.string.promo_code_use_button)
            )
          }
        }
      } else {
        AppItem(
          app = appState.app,
          onClick = {},
        ) {
          InstallViewShort(
            app = appState.app,
            onInstallStarted = {}
          )
        }
      }
    }

    is AppUiState.Loading -> LoadingView()
    is AppUiState.Error -> BottomSheetGenericErrorView(onRetryClick = reload)
    is AppUiState.NoConnection -> BottomSheetNoConnectionView(onRetryClick = reload)
  }
}

@Composable
private fun WalletInstallSection(
  walletApp: App,
) {
  Image(
    imageVector = getBonusTextIcon(Palette.Secondary, Palette.Primary, Palette.Black),
    contentDescription = null,
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 24.dp)
  )
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.padding(bottom = 8.dp)
  ) {
    Image(
      imageVector = getBonusIcon(
        giftColor = Palette.Primary,
        outlineColor = Palette.Black,
      ),
      contentDescription = null,
    )
    Text(
      text = stringResource(
        id = R.string.promo_code_install_wallet_banner_title,
        "20" //TODO Hardcoded value (should come from backend in the future)
      ),
      color = Palette.White,
      style = AGTypography.InputsM
    )
  }
  Text(
    text = stringResource(id = R.string.promo_code_install_wallet_banner_body),
    color = Palette.White,
    style = AGTypography.Body,
  )
  AppItem(
    app = walletApp,
    onClick = {},
    modifier = Modifier.padding(bottom = 8.dp)
  ) {
    InstallViewShort(app = walletApp)
  }
  Divider(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 12.dp)
  )
}

@Composable
private fun LoadingView() {
  Column(
    modifier = Modifier
      .defaultMinSize(minHeight = 376.dp)
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    IndeterminateCircularLoading(color = Palette.Primary)
  }
}

@PreviewDark
@Composable
fun PreviewBottomSheetContent() {
  AptoideTheme(darkTheme = true) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp)) {
      BottomSheetHeader()
      PromoCodeBottomSheetContent(
        promoCodeApp = randomPromoCodeApp,
        walletApp = walletApp,
        showSnack = { }
      )
    }
  }
}
