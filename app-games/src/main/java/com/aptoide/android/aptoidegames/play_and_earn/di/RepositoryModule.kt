package com.aptoide.android.aptoidegames.play_and_earn.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.aptoide.android.aptoidegames.play_and_earn.data.DefaultUserInfoRepository
import com.aptoide.android.aptoidegames.play_and_earn.data.UserAccountPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.data.UserInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.userAccountPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "userAccountPreferences"
)

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  @UserAccountPreferencesDataStore
  fun provideUserPreferencesDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.userAccountPreferencesDataStore
  }

  @Singleton
  @Provides
  fun provideUserInfoRepository(
    userAccountPreferencesRepository: UserAccountPreferencesRepository
  ): UserInfoRepository {
    return DefaultUserInfoRepository(userAccountPreferencesRepository)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserAccountPreferencesDataStore
