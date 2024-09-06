package com.aptoide.android.aptoidegames.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.environment_info.DeviceInfo
import cm.aptoide.pt.extensions.runPreviewable
import com.appcoins.payments.arch.WalletProvider
import com.appcoins.payments.di.Payments
import com.appcoins.payments.di.walletProvider
import com.aptoide.android.aptoidegames.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
  val deviceInfo: DeviceInfo,
  @StoreName val storeName: String,
) : ViewModel()

class DeviceInfoViewModel(
  private val deviceInfo: DeviceInfo,
  private val walletProvider: WalletProvider,
  private val storeName: String,
) : ViewModel() {

  private val viewModelState = MutableStateFlow("")

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      val pid = runCatching { walletProvider.getWallet() }.getOrNull()?.address ?: "-"
      viewModelState.update {
        "${deviceInfo.getDeviceInfoSummary()}\n" +
          "AptoideGames: ${Integer.toHexString(storeName.hashCode())}\n" +
          "PID: $pid\n" +
          "AptoideGames version: ${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})\n"
      }
    }
  }
}

@Composable
fun rememberDeviceInfo(): String = runPreviewable(
  preview = { "" },
  real = {
    val injectionsProvider = hiltViewModel<InjectionsProvider>()
    val vm: DeviceInfoViewModel = viewModel(
      factory = object : Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return DeviceInfoViewModel(
            deviceInfo = injectionsProvider.deviceInfo,
            walletProvider = Payments.walletProvider,
            storeName = injectionsProvider.storeName
          ) as T
        }
      }
    )

    val deviceInfo by vm.uiState.collectAsState()
    deviceInfo
  }
)

fun ClipboardManager.setText(text: String) = setText(AnnotatedString(text))
