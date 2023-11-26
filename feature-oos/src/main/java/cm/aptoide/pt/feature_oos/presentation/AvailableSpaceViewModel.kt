package cm.aptoide.pt.feature_oos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.OutOfSpaceException
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AvailableSpaceViewModel constructor(
  packageName: String,
  installManager: InstallManager,
  private val installPackageInfo: InstallPackageInfo,
) : ViewModel() {

  private val app = installManager.getApp(packageName)

  private val viewModelState = MutableStateFlow(0L)

  val availableSpaceState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    installManager.appsChanges
      .onStart { emit(app) }
      .map { (app.canInstall(installPackageInfo) as? OutOfSpaceException)?.missingSpace ?: 0 }
      .catch { throwable -> throwable.printStackTrace() }
      .onEach { requiredSpace ->
        viewModelState.update { requiredSpace }
      }
      .launchIn(viewModelScope)
  }
}
