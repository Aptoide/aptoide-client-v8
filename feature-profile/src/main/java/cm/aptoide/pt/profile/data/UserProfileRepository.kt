package cm.aptoide.pt.profile.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cm.aptoide.pt.profile.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

  suspend fun createUser(user: UserProfile){
    dataStore.edit { bundlePreferences ->
      bundlePreferences[USER_NAME] = user.username
      bundlePreferences[USER_IMAGE] = user.userImage
      bundlePreferences[USER_JOINED_DATA] = user.joinedData
      bundlePreferences[USER_STORE] = user.userStore
    }
  }

  suspend fun setUser(user: UserProfile){
    dataStore.edit { bundlePreferences ->
      bundlePreferences[USER_NAME] = user.username
      bundlePreferences[USER_IMAGE] = user.userImage
      bundlePreferences[USER_STORE] = user.userStore
    }
  }

  suspend fun deleteUser() {
    dataStore.edit { bundlePreferences ->
      bundlePreferences.remove(USER_NAME)
      bundlePreferences.remove(USER_IMAGE)
      bundlePreferences.remove(USER_JOINED_DATA)
      bundlePreferences.remove(USER_STORE)
    }
  }

  fun getUser(): Flow<UserProfile> = dataStore.data.map {
    UserProfile(
      username = it[USER_NAME] ?: "",
      userImage = it[USER_IMAGE] ?: "",
      joinedData = it[USER_JOINED_DATA] ?: "",
      userStore = it[USER_STORE] ?: "",
    )
  }

  private companion object {
    val USER_NAME = stringPreferencesKey("username")
    val USER_IMAGE = stringPreferencesKey("userImage")
    val USER_JOINED_DATA = stringPreferencesKey("userJoinedData")
    val USER_STORE = stringPreferencesKey("userStore")
  }
}
