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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.aptoide.android.aptoidegames.home.AppThemeViewModel
import com.aptoide.android.aptoidegames.settings.rememberDeviceInfo
import com.aptoide.android.aptoidegames.support.SupportView
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportActivity : AppCompatActivity() {

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
            title = "Contact Support", //TODO
            placeholderText = "Please give us all the details: \n- When did the problem started\n- On which game are you making a purchase\n\nOnce you tap on Send, your mail app will open.\nFeel free to attach a screenshot or video about the issue!", //TODO
            deviceInfo = deviceInfo,
            email = "aptoide.games@appcoins.io",
            context = localContext,
            subject = "AG Contact Support", //TODO
            navigateBack = ::finish
          )

          TYPE_FEEDBACK -> SupportView(
            title = stringResource(R.string.settings_feedback_title),
            placeholderText = stringResource(R.string.settings_send_feedback_body),
            deviceInfo = deviceInfo,
            email = "aptoide.games@aptoide.zendesk.com",
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

    fun openForFeedBack(context: Context) = open(context, TYPE_FEEDBACK)

    fun openForSupport(context: Context) = open(context, TYPE_SUPPORT)
    private fun open(
      context: Context,
      type: String,
    ) {
      val intent = Intent(context, SupportActivity::class.java)
        .apply { putExtra("type", type) }
      context.startActivity(intent)
    }
  }
}
