package com.aptoide.android.aptoidegames.network.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.install_manager.environment.NetworkConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkStateInjectionsProvider @Inject constructor(
  val networkConnection: NetworkConnection
) : ViewModel()

@Composable
fun rememberNetworkState(): NetworkConnection.State? = runPreviewable(
  preview = { NetworkConnection.State.entries.toTypedArray().random() },
  real = {
    val vm = hiltViewModel<NetworkStateInjectionsProvider>()
    var state by remember { mutableStateOf(vm.networkConnection.state) }

    vm.networkConnection.setOnChangeListener {
      state = it
    }

    state
  }
)
