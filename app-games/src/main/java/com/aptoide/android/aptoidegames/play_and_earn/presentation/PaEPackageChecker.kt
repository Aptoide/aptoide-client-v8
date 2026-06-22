package com.aptoide.android.aptoidegames.play_and_earn.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.campaigns.domain.GetAvailablePaEPackagesUseCase
import cm.aptoide.pt.campaigns.domain.GetCachedPaEAppUseCase
import cm.aptoide.pt.campaigns.domain.PaEApp
import cm.aptoide.pt.campaigns.domain.randomPaEApp
import cm.aptoide.pt.extensions.runPreviewable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaEPackagesProvider @Inject constructor(
  val availablePackagesUseCase: GetAvailablePaEPackagesUseCase,
  val getCachedPaEAppUseCase: GetCachedPaEAppUseCase,
) : ViewModel()

@Composable
fun rememberIsPackageInPaE(packageName: String): Boolean = runPreviewable(
  preview = { false },
  real = {
    val vm = hiltViewModel<PaEPackagesProvider>()
    var isPackageInPaE by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(packageName) {
      isPackageInPaE = vm.availablePackagesUseCase().getOrNull()?.contains(packageName) ?: false
    }

    isPackageInPaE
  }
)

// The cached P&E app for the package, or null while loading or when it isn't part of a campaign.
@Composable
fun rememberPaEApp(packageName: String): PaEApp? = runPreviewable(
  preview = { randomPaEApp },
  real = {
    val vm = hiltViewModel<PaEPackagesProvider>()
    var app by remember { mutableStateOf<PaEApp?>(null) }

    LaunchedEffect(packageName) {
      app = vm.getCachedPaEAppUseCase(packageName).getOrNull()
    }

    app
  }
)
