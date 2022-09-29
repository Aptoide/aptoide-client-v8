package cm.aptoide.pt

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import cm.aptoide.pt.home.BottomNavigationManager
import cm.aptoide.pt.home.MainView
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var bottomNavigationManager: BottomNavigationManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AptoideTheme {
        MainView(bottomNavigationManager.shouldShowBottomNavigation())
      }
    }
  }
}