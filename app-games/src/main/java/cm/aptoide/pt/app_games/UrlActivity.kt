package cm.aptoide.pt.app_games

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.app_games.home.AppThemeViewModel
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.app_games.toolbar.SimpleAppGamesToolbar
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

@AndroidEntryPoint
class UrlActivity : AppCompatActivity() {

  @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val url = intent.getStringExtra("url")
    setContent {
      val themeViewModel = hiltViewModel<AppThemeViewModel>()
      val isDarkTheme by themeViewModel.uiState.collectAsState()
      AptoideTheme(darkTheme = isDarkTheme ?: isSystemInDarkTheme()) {
        Scaffold(
          topBar = { SimpleAppGamesToolbar() }
        ) {
          url?.let {
            UrlView(url = URLDecoder.decode(it, UTF_8.toString()))
          }
        }
      }
    }
  }

  companion object {
    fun open(context: Context, url: String) {
      val intent = Intent(context, UrlActivity::class.java)
        .apply { putExtra("url", URLEncoder.encode(url, UTF_8.toString())) }
      context.startActivity(intent)
    }
  }
}
