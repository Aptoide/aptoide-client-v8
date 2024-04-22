package cm.aptoide.pt.app_games

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.app_games.home.MainView
import cm.aptoide.pt.app_games.installer.notifications.InstallerNotificationsBuilder
import cm.aptoide.pt.install_manager.InstallManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var installManager: InstallManager

  private var navController: NavHostController? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
        .also { this.navController = it }

      MainView(navController)

      LaunchedEffect(key1 = navController) {
        handleNotificationIntent(intent = intent)
      }
    }
  }

  private fun handleNotificationIntent(intent: Intent?) {
    CoroutineScope(Dispatchers.IO).launch {
      intent?.getStringExtra(InstallerNotificationsBuilder.ALLOW_METERED_DOWNLOAD_FOR_PACKAGE)
        ?.let(installManager::getApp)
        ?.task
        ?.allowDownloadOnMetered()
    }
    intent.agDeepLink?.let {
      navController?.navigate(it)
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    navController?.handleDeepLink(intent)
    handleNotificationIntent(intent)
  }
}
