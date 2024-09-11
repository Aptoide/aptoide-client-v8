package com.aptoide.android.aptoidegames.wallet

import androidx.compose.runtime.Composable
import cm.aptoide.pt.feature_apps.domain.AppSource
import cm.aptoide.pt.feature_apps.presentation.rememberApp

const val WALLET_PACKAGE_NAME = "com.appcoins.wallet"

@Composable
fun rememberWalletApp() = rememberApp(
  source = AppSource
    .of(appId = null, packageName = WALLET_PACKAGE_NAME)
    .asSource()
)
