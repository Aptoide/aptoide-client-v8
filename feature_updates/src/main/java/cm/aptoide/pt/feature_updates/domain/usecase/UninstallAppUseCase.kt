package cm.aptoide.pt.feature_updates.domain.usecase

import android.content.Context
import android.util.Log
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class UninstallAppUseCase @Inject constructor(private val context: Context) {

  fun uninstallApp(packageName: String) {
    Log.d("Uninstall", "uninstallApp: this will require installations")
  }

}