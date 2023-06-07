package cm.aptoide.pt

import android.os.Bundle
import androidx.activity.compose.setContent
import cm.aptoide.pt.home.BottomNavigationManager
import cm.aptoide.pt.home.MainView
import cm.aptoide.pt.installer.platform.InstallActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : InstallActivity() {

  @Inject
  lateinit var bottomNavigationManager: BottomNavigationManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MainView(bottomNavigationManager.shouldShowBottomNavigation())
    }
  }
}