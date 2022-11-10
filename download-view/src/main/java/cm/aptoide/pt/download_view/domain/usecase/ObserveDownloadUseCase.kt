package cm.aptoide.pt.download_view.domain.usecase

import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.aptoide_installer.model.Download
import cm.aptoide.pt.feature_apps.data.App
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class ObserveDownloadUseCase @Inject constructor(private val installManager: InstallManager) {

  fun getDownload(app: App): Flow<Download> {
    return installManager.getDownload(app)
  }
}