package com.aptoide.android.aptoidegames.wallet

import androidx.compose.runtime.Composable
import cm.aptoide.pt.feature_apps.presentation.rememberApp
import cm.aptoide.pt.feature_apps.presentation.toPackageNameParam

const val WALLET_PACKAGE_NAME = "com.appcoins.wallet"

@Composable
fun rememberWalletApp() = rememberApp(source = WALLET_PACKAGE_NAME.toPackageNameParam())
