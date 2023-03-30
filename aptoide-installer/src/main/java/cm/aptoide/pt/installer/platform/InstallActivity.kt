package cm.aptoide.pt.installer.platform

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Activity to handel the installation sessions result events in [onCreate] and [onNewIntent]
 *
 * Implicitly relays on the fact that we will get [onNewIntent] called before getting the result
 * in [launcher]
 */

@AndroidEntryPoint
open class InstallActivity : AppCompatActivity() {

  // A Singleton instance
  @Inject
  lateinit var installEvents: InstallEventsImpl

  // Used to launch user action intent and report when it's closed
  private lateinit var launcher: ActivityResultLauncher<Intent>

  fun launchUserAction(intent: Intent) = launcher.launch(intent)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Initialise the launcher
    launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      installEvents.onResult()
    }
    // Update the activity in the installer
    installEvents.currentActivity = this
  }

  override fun onDestroy() {
    super.onDestroy()
    // Set activity in the installer to null if it is still this one
    // To make sure we will not override another working activity that already declared itself
    if (installEvents.currentActivity == this) {
      installEvents.currentActivity = null
    }
  }
}
