package com.aptoide.android.aptoidegames.developer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.agDeveloperPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "agDeveloperPreferences"
)

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Singleton
  @Provides
  @AGDeveloperPreferencesDataStore
  fun provideAGDeveloperPreferencesDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.agDeveloperPreferencesDataStore
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AGDeveloperPreferencesDataStore
