package com.aptoide.android.aptoidegames.promo_codes

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.download_view.presentation.DownloadUiState
import cm.aptoide.pt.download_view.presentation.rememberDownloadState
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getPackageInfo
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.rememberWalletApp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.BottomSheetHeader
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.design_system.TertiarySmallButton
import com.aptoide.android.aptoidegames.drawables.icons.getAppcoinsIconWithBackground
import com.aptoide.android.aptoidegames.drawables.icons.getBonusTextIcon
import com.aptoide.android.aptoidegames.error_views.BottomSheetGenericErrorOkView
import com.aptoide.android.aptoidegames.error_views.BottomSheetGenericErrorView
import com.aptoide.android.aptoidegames.error_views.BottomSheetNoConnectionView
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.feature_apps.presentation.rememberAppIconDrawable
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
    val context = LocalContext.current
    val packageInfo = remember { context.packageManager.getPackageInfo(promoCodeApp.packageName) }

    val promoCode = promoCodeApp.promoCode
    val (walletAppUiState, walletReload) = rememberWalletApp()

    val promoCodeAnalytics = rememberPromoCodeAnalytics()

    LaunchedEffect(packageInfo) {
      if (packageInfo == null) {
        promoCodeAnalytics.sendPromoCodeImpressionEvent(status = "app_not_installed")
      }
    }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 32.dp)) {
      BottomSheetHeader()
      if (packageInfo != null) {
        val appName = packageInfo.applicationInfo!!.loadLabel(context.packageManager).toString()
        val icon =
          rememberAppIconDrawable(packageName = promoCodeApp.packageName, context = context)
        PromoCodeBottomSheetContent(
          appName = appName,
          appIcon = icon,
          walletAppUiState = walletAppUiState,
          dismiss = dismiss,
          navigate = navigate,
          promoCode = promoCode,
          walletReload = walletReload,
          showSnack = showSnack
        )
      } else {
        BottomSheetGenericErrorOkView(onOkClick = dismiss)
      }
    }
  }
}

@Composable
fun PromoCodeBottomSheetContent(
  appName: String,
  appIcon: Drawable?,
  walletAppUiState: AppUiState,
  dismiss: () -> Unit,
  navigate: (String) -> Unit,
  promoCode: String,
  walletReload: () -> Unit,
  showSnack: (String) -> Unit
) {
  when (walletAppUiState) {
    is AppUiState.Idle -> {
      val context = LocalContext.current
      val walletDisclaimer = stringResource(id = R.string.promo_code_install_wallet_disclaimer)
      val walletApp = walletAppUiState.app
      val downloadState = rememberDownloadState(app = walletApp)
      val showWalletSegment = remember { mutableStateOf(false) }
      val isWalletInstalled = downloadState is DownloadUiState.Installed
        || downloadState is DownloadUiState.Outdated

      val promoCodeAnalytics = rememberPromoCodeAnalytics()

      LaunchedEffect(Unit) {
        promoCodeAnalytics.sendPromoCodeImpressionEvent(
          status = "success",
          withWallet = isWalletInstalled
        )
      }

      LaunchedEffect(key1 = downloadState) {
        when (downloadState) {
          is DownloadUiState.Installed -> Unit
          else -> showWalletSegment.value = true
        }
      }
      if (showWalletSegment.value) {
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
            imageVector = getAppcoinsIconWithBackground(Palette.AppCoinsPink, Palette.White),
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
          onClick = {
            navigate(
              buildAppViewRoute(walletApp)
            )
            dismiss()
          },
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

      Text(
        modifier = Modifier.padding(top = 4.dp),
        text = stringResource(
          id = R.string.promo_code_extra_title,
          "10" //TODO Hardcoded value (should come from backend in the future)
        ),
        color = Palette.White,
        style = AGTypography.Title
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        AptoideAsyncImage(
          modifier = Modifier.size(64.dp),
          data = appIcon,
          contentDescription = null,
        )
        Text(
          text = appName,
          style = AGTypography.DescriptionGames,
          overflow = TextOverflow.Ellipsis,
          maxLines = 2,
          color = Palette.White,
          modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .weight(1f),
        )
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
              val uri = Uri.parse("appcoins://promocode?promocode=$promoCode")
              val intent = Intent(Intent.ACTION_VIEW, uri)
              context.startActivity(intent)
              promoCodeAnalytics.sendPromoCodeClickEvent(withWallet = true)
            },
            title = stringResource(id = R.string.promo_code_use_button)
          )
        }
      }
    }

    is AppUiState.Loading -> LoadingView()
    is AppUiState.Error -> BottomSheetGenericErrorView(onRetryClick = walletReload)
    is AppUiState.NoConnection -> BottomSheetNoConnectionView(onRetryClick = walletReload)
  }
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
        appName = getRandomString(1..10),
        appIcon = getDrawable(LocalContext.current, R.mipmap.ic_launcher)!!,
        walletAppUiState = AppUiState.Idle(randomApp),
        dismiss = { },
        navigate = { },
        promoCode = "123",
        walletReload = { },
        showSnack = { }
      )
    }
  }
}
