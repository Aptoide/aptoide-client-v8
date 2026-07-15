package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

import android.app.AppOpsManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.PaEAnalytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UsageStatsPermissionActivity : AppCompatActivity() {

  @Inject
  lateinit var paEAnalytics: PaEAnalytics

  private lateinit var appOps: AppOpsManager

  private var permissionCheckJob: Job? = null
  private val checkInterval = 600L
  private var grantedReported = false

  val overlayPermissionLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
      reportGrantedIfNeeded()
      bringAppToForeground()
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager

    startPermissionCheck()

    overlayPermissionLauncher.launch(
      Intent(
        Settings.ACTION_USAGE_ACCESS_SETTINGS,
        ("package:$packageName").toUri()
      )
    )
  }

  private fun startPermissionCheck() {
    permissionCheckJob = lifecycleScope.launch(Dispatchers.Main.immediate) {
      while (true) {
        if (hasUsageStatsPermissionStatus(appOps)) {
          reportGrantedIfNeeded()
          bringAppToForeground()
          break
        }

        delay(checkInterval)
      }
    }
  }

  private fun reportGrantedIfNeeded() {
    if (!grantedReported && hasUsageStatsPermissionStatus(appOps)) {
      grantedReported = true
      paEAnalytics.sendPaEUsageAccessPermissionGranted()
    }
  }

  private fun bringAppToForeground() {
    val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
    launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(launchIntent)
    finish()
  }

  override fun onDestroy() {
    super.onDestroy()
    permissionCheckJob?.cancel()
  }
}
