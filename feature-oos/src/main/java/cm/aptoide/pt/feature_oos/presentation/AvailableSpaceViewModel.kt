package cm.aptoide.pt.feature_oos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoManager
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.OutOfSpaceException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AvailableSpaceViewModel(
  app: App,
  installManager: InstallManager,
  private val installPackageInfoManager: InstallPackageInfoManager,
) : ViewModel() {

  private val appInstaller = installManager.getApp(app.packageName)

  private val viewModelState = MutableStateFlow(0L)

  val availableSpaceState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    installManager.appsChanges
      .onStart { emit(appInstaller) }
      .map { (appInstaller.canInstall(installPackageInfoManager.get(app)) as? OutOfSpaceException)?.missingSpace ?: 0 }
      .catch { throwable -> throwable.printStackTrace() }
      .onEach { requiredSpace ->
        viewModelState.update { requiredSpace }
      }
      .launchIn(viewModelScope)
  }
}
