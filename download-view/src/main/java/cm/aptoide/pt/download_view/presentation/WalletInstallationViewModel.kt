package cm.aptoide.pt.download_view.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("OPT_IN_USAGE")
@HiltViewModel
class WalletInstallationViewModel @Inject constructor(
  installManager: InstallManager
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
        appInstaller.packageInfo,
        appInstaller.tasks.flatMapConcat { it?.stateAndProgress ?: flowOf(null) }
      ) { packageInfo, task -> Pair(packageInfo, task) }
        .catch { throwable -> throwable.printStackTrace() }
        .collect { (info, task) ->
          viewModelState.update { info != null || task != null }
        }
    }
  }
}

@Composable
fun walletInstalled(): Boolean {
  val walletViewModel = hiltViewModel<WalletInstallationViewModel>()
  val walletUiState by walletViewModel.uiState.collectAsState()
  return walletUiState
}
