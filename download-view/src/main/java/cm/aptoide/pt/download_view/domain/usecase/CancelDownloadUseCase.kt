package cm.aptoide.pt.download_view.domain.usecase

import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.feature_apps.data.DetailedApp
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class CancelDownloadUseCase @Inject constructor(private val installManager: InstallManager) {

  suspend fun cancelDownload(app: DetailedApp) {
    installManager.cancelDownload(app.md5)
  }

}
