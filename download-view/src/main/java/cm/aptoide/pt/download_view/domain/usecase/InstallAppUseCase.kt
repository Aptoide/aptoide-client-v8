package cm.aptoide.pt.download_view.domain.usecase

import cm.aptoide.pt.download_view.domain.model.AppDetailsMapper
import cm.aptoide.pt.download_view.domain.model.getInstallPackageInfo
import cm.aptoide.pt.download_view.domain.model.lastTaskState
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.install_manager.Task
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ViewModelScoped
class InstallAppUseCase<D> @Inject constructor(
  private val installManager: InstallManager<D>,
  private val appDetailsMapper: AppDetailsMapper<D>,
) {

  suspend fun getCurrentState(app: App): Flow<Pair<Task.State, Int>?> = flow {
    installManager
      .getApp(app.packageName, appDetailsMapper.toDetails(app))
      .run {
        emit(lastTaskState())
        getTask()?.stateAndProgress?.let { emitAll(it) }
      }
  }

  suspend fun install(app: App): Flow<Pair<Task.State, Int>> =
    installManager.getApp(app.packageName)
      .install(app.getInstallPackageInfo())
      .stateAndProgress

  suspend fun cancelInstallation(app: App) {
    installManager.getApp(app.packageName).getTask()?.cancel()
  }
}
