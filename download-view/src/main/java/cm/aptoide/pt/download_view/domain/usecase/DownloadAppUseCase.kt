package cm.aptoide.pt.download_view.domain.usecase

import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.feature_apps.data.App
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class DownloadAppUseCase @Inject constructor(private val installManager: InstallManager) {

  fun downloadApp(app: App) {
    // TODO: create download object of the install manager
    //installManager.download()
  }
}