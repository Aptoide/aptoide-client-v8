package cm.aptoide.pt.download_view.domain.usecase

import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class OpenAppUseCase @Inject constructor(private val installedAppOpener: InstalledAppOpener) {
  fun openApp(packageName: String) {
    installedAppOpener.openInstalledApp(packageName)
  }

}
