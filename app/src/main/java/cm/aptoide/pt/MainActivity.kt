package cm.aptoide.pt

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController
import cm.aptoide.pt.home.MainView
import cm.aptoide.pt.theme.AptoideTheme


class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setContent {
      AptoideTheme {
        MainView()
      }
    }
  }
}