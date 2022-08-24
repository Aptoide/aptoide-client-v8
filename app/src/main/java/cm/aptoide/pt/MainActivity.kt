package cm.aptoide.pt

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.home.MainView
import cm.aptoide.pt.theme.AptoideTheme
import dagger.hilt.android.AndroidEntryPoint

private val android.content.Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
  name = "settings")

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AptoideTheme {
        MainView(dataStore)
      }
    }
  }
}