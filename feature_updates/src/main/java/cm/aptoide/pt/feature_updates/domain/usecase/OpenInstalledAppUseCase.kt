package cm.aptoide.pt.feature_updates.domain.usecase

import android.content.Context
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class OpenInstalledAppUseCase @Inject constructor(val context: Context) {

  fun openInstalledApp(packageName: String) {
    val intentForPackage = context.packageManager.getLaunchIntentForPackage(packageName)
    context.startActivity(intentForPackage)
  }
}