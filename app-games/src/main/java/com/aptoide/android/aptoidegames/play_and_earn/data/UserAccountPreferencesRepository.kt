package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aptoide.android.aptoidegames.play_and_earn.di.UserAccountPreferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.domain.UserInfo
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserAccountPreferencesRepository @Inject constructor(
  @UserAccountPreferencesDataStore private val dataStore: DataStore<Preferences>,
) {

  companion object PreferencesKeys {
    private val USER_INFO = stringPreferencesKey("user_info")
  }

  suspend fun setUserInfo(userInfo: UserInfo?) {
    dataStore.edit { preferences ->
      if (userInfo == null) {
        preferences.remove(USER_INFO)
      } else {
        val userInfoStr = Gson().toJson(userInfo)
        preferences[USER_INFO] = userInfoStr
      }
    }
  }

  fun getUserInfo(): Flow<UserInfo?> =
    dataStore.data.map { preferences ->
      preferences[USER_INFO]?.let { Gson().fromJson(it, UserInfo::class.java) }
    }

  suspend fun clearUserInfo() = dataStore.edit { preferences ->
    preferences.remove(USER_INFO)
  }
}
