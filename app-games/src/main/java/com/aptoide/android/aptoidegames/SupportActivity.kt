package com.aptoide.android.aptoidegames

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.wallet.datastore.WalletCoreDataSource
import com.aptoide.android.aptoidegames.home.AppThemeViewModel
import com.aptoide.android.aptoidegames.settings.rememberDeviceInfo
import com.aptoide.android.aptoidegames.support.SupportView
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SupportActivity : AppCompatActivity() {

  @Inject
  lateinit var walletCoreDataSource: WalletCoreDataSource

  @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val type = intent.getStringExtra(TYPE)
    setContent {
      val themeViewModel = hiltViewModel<AppThemeViewModel>()
      val isDarkTheme by themeViewModel.uiState.collectAsState()
      AptoideTheme(darkTheme = isDarkTheme ?: isSystemInDarkTheme()) {
        val deviceInfo = rememberDeviceInfo()
        val localContext = LocalContext.current
        when (type) {
          TYPE_SUPPORT -> SupportView(
            type = type,
            title = stringResource(id = R.string.contact_support_title),
            placeholderText = stringResource(id = R.string.contact_support_field_body),
            deviceInfo = deviceInfo,
            email = "aptoide.games@appcoins.io",
            context = localContext,
            subject = "AG Contact Support", //No need for translations so it's hardcoded
            navigateBack = ::finish
          )

          // AND-951: an appeal from the restricted Play & Earn screen. The subject says why, and
          // the wallet address lets support find the account; it only goes into the e-mail the
          // user chooses to send.
          TYPE_RESTRICTED -> {
            val walletAddress by produceState<String?>(null) {
              value = runCatching { walletCoreDataSource.getCurrentWalletAddress() }.getOrNull()
            }
            SupportView(
              type = type,
              title = stringResource(id = R.string.contact_support_title),
              placeholderText = stringResource(id = R.string.contact_support_field_body),
              deviceInfo = deviceInfo,
              accountInfo = walletAddress,
              email = FEEDBACK_EMAIL,
              context = localContext,
              subject = stringResource(R.string.play_and_earn_exchange_error_restricted_title),
              navigateBack = ::finish
            )
          }

          TYPE_FEEDBACK -> SupportView(
            type = type,
            title = stringResource(R.string.settings_feedback_title),
            placeholderText = stringResource(R.string.settings_send_feedback_body),
            deviceInfo = deviceInfo,
            email = FEEDBACK_EMAIL,
            context = localContext,
            subject = stringResource(R.string.app_info_send_email_subject),
            navigateBack = ::finish
          )
        }
      }
    }
  }

  companion object {
    const val TYPE = "type"
    const val TYPE_FEEDBACK = "feedback"
    const val TYPE_SUPPORT = "support"
    const val TYPE_RESTRICTED = "pae_restricted"

    fun openForFeedBack(context: Context) = open(context, TYPE_FEEDBACK)

    fun openForSupport(context: Context) = open(context, TYPE_SUPPORT)

    /** The Play & Earn restricted account's appeal (AND-951). */
    fun openForRestrictedAccount(context: Context) = open(context, TYPE_RESTRICTED)

    private fun open(
      context: Context,
      type: String,
    ) {
      val intent = Intent(context, SupportActivity::class.java)
        .apply { putExtra(TYPE, type) }
      context.startActivity(intent)
    }
  }
}
