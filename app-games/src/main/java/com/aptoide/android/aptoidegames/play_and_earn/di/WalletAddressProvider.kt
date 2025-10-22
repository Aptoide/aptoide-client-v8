package com.aptoide.android.aptoidegames.play_and_earn.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val walletCoreDataStore: WalletCoreDataSource
) : ViewModel()

@Composable
fun rememberWalletAddress(): String? = runPreviewable(
  preview = { "wallet address" },
  real = {

    val vm = hiltViewModel<InjectionsProvider>()
    var walletAddress: String? by remember { mutableStateOf(null) }
    LaunchedEffect(Unit) {
      walletAddress = vm.walletCoreDataStore.getCurrentWalletAddress()
    }
    walletAddress
  }
)
