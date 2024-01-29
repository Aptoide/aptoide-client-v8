package cm.aptoide.pt.download_view.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class WalletInstallationViewModel @Inject constructor(
  installManager: InstallManager,
  featureFlags: FeatureFlags,
) : ViewModel() {

  private val appInstaller = installManager.getApp("com.appcoins.wallet")

  private val viewModelState = MutableStateFlow(value = false)

  val uiState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    viewModelScope.launch {
      combine(
        appInstaller.packageInfoFlow,
        appInstaller.taskFlow.flatMapConcat { it?.stateAndProgress ?: flowOf(null) },
      ) { packageInfo, task -> Pair(packageInfo, task) }
        .catch { throwable -> throwable.printStackTrace() }
        .collect { (info, task) ->
          val enabled = featureFlags.get("enable_wallet_companion_app").equals("true")
          viewModelState.update { enabled && info == null && task == null }
        }
    }
  }
}

@Composable
fun shouldInstallWallet(): Boolean = runPreviewable(
  preview = { false },
  real = {
    val walletViewModel = hiltViewModel<WalletInstallationViewModel>()
    val walletUiState by walletViewModel.uiState.collectAsState()
    walletUiState
  },
)
