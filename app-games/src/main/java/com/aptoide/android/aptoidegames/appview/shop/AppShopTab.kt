package com.aptoide.android.aptoidegames.appview.shop

// HACKATHON: not localized (except the tab title).

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.isInCatappult
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.design_system.PrimaryTextButton
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.playAndEarnSignInRoute
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.wallet_web.WALLET_WEB_TAB_ENABLED
import com.aptoide.android.aptoidegames.wallet_web.WalletWebReturn
import com.aptoide.android.aptoidegames.wallet_web.walletWebRoute

@Composable
private fun rememberAppShopViewModel(packageName: String): AppShopViewModel =
  hiltViewModel(key = "appShop_$packageName")

/** True when the AppView should show the Shop tab for [app]. */
@Composable
fun rememberShowShopTab(app: App): Boolean = runPreviewable(
  preview = { false },
  real = {
    val vm = rememberAppShopViewModel(app.packageName)
    val installedFlow = remember(app.packageName) { vm.isInstalled(app.packageName) }
    val installed by installedFlow.collectAsState(initial = false)
    shouldShowShopTab(installed, app.isAppCoins, app.isInCatappult())
  }
)

@Composable
fun AppShopTab(
  app: App,
  navigate: (String) -> Unit,
) {
  val vm = rememberAppShopViewModel(app.packageName)
  val uiState by vm.uiState.collectAsState()
  LaunchedEffect(app.packageName) { vm.load(app.packageName, app.name) }

  val requestSignIn = {
    if (WALLET_WEB_TAB_ENABLED) {
      WalletWebReturn.returnAfterLogin = true
      navigate(walletWebRoute)
    } else {
      navigate(playAndEarnSignInRoute)
    }
  }

  Column(
    modifier = Modifier.padding(top = 12.dp, bottom = 48.dp, start = 16.dp, end = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    when (val state = uiState) {
      ShopUiState.Loading -> Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
      ) {
        IndeterminateCircularLoading(color = Palette.Primary)
      }

      is ShopUiState.Loaded -> {
        Text(
          text = "Buy items for ${app.name} with your Aptoide wallet. The game opens to " +
            "finish the purchase.",
          style = AGTypography.DescriptionGames,
          color = Palette.GreyLight,
        )
        if (state.rows.isEmpty()) {
          Text(
            text = "${app.name} has no items for sale right now.",
            style = AGTypography.InputsM,
            color = Palette.White,
          )
        }
        state.rows.forEach { row ->
          ShopProductRow(
            app = app,
            row = row,
            onBuy = { vm.buy(row.product.sku, requestSignIn) },
          )
        }
        if (!state.signedIn) {
          Text(
            text = "Buy asks you to sign in to your Aptoide wallet first.",
            style = AGTypography.DescriptionGames,
            color = Palette.GreyLight,
          )
          if (BuildConfig.DEBUG) {
            // HACKATHON: debug-only, buys without a wallet session (no Google/wallet sign-in).
            PrimaryTextButton(
              onClick = vm::continueAsDemoUser,
              text = "Continue as demo user",
              color = Palette.GreyLight,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun ShopProductRow(
  app: App,
  row: ShopRow,
  onBuy: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Palette.GreyDark)
      .border(
        width = if (row.state == ShopRowState.PAID) 1.dp else 0.dp,
        color = if (row.state == ShopRowState.PAID) Palette.Primary else Palette.GreyDark,
      )
      .padding(12.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      AptoideAsyncImage(
        modifier = Modifier.size(48.dp),
        data = app.icon,
        contentDescription = null,
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = row.product.title,
          style = AGTypography.InputsM,
          color = Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = row.product.priceLabel,
          style = AGTypography.InputsM,
          color = Palette.Primary,
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      when (row.state) {
        ShopRowState.IDLE -> PrimarySmallButton(onClick = onBuy, title = "Buy")
        ShopRowState.WAITING_FOR_GAME -> PrimarySmallButton(
          onClick = {},
          enabled = false,
          title = "In ${app.name}…",
        )

        ShopRowState.PAID -> Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = Palette.Primary,
            modifier = Modifier.size(20.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "Paid", style = AGTypography.InputsM, color = Palette.Primary)
        }
      }
    }
    row.message?.let {
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = it, style = AGTypography.DescriptionGames, color = Palette.GreyLight)
    }
  }
}
