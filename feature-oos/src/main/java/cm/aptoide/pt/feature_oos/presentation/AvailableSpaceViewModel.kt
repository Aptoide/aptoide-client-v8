package cm.aptoide.pt.feature_oos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.install_manager.InstallManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AvailableSpaceViewModel(
  app: App,
  private val installManager: InstallManager,
  private val installPackageInfoMapper: InstallPackageInfoMapper,
) : ViewModel() {

  private val viewModelState = MutableStateFlow(0L)

  val availableSpaceState = viewModelState
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      viewModelState.value
    )

  init {
    installManager.appsChanges
      .map {}
      .onStart { emit(Unit) }
      .map { installManager.getMissingFreeSpaceFor(installPackageInfoMapper.map(app)) }
      .onEach { requiredSpace -> viewModelState.update { requiredSpace } }
      .launchIn(viewModelScope)
  }
}
