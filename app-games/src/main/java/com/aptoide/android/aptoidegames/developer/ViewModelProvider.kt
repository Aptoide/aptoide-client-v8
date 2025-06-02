package com.aptoide.android.aptoidegames.developer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.runPreviewable

@Composable
fun rememberAGDeveloperOptions(): Pair<Boolean, (Boolean) -> Unit> = runPreviewable(
  preview = { false to {} },
  real = {
    val vm: AGDeveloperOptionsViewModel = hiltViewModel<AGDeveloperOptionsViewModel>()
    val uiState by vm.areAGDeveloperOptionsEnabled.collectAsState()
    uiState.let {
      it to vm::setAGDeveloperOptionsState
    }
  }
)
