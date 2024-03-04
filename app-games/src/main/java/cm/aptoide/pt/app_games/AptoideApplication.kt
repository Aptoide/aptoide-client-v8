package cm.aptoide.pt.app_games

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "userPreferences",
  produceMigrations = { context ->
    listOf(SharedPreferencesMigration({ PreferenceManager.getDefaultSharedPreferences(context) }))
  }
)
@HiltAndroidApp
class AptoideApplication : Application() {


}
