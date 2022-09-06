package cm.aptoide.pt.feature_updates.domain.usecase

import cm.aptoide.pt.feature_updates.presentation.InstalledAppOpener
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class OpenInstalledAppUseCase @Inject constructor(private val installedAppOpener: InstalledAppOpener) {

  fun openInstalledApp(packageName: String) {
    installedAppOpener.openInstalledApp(packageName)
  }
}