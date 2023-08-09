package cm.aptoide.pt.feature_flags.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.feature_flags.data.SettingsLocalRepositoryImpl
import cm.aptoide.pt.feature_flags.data.FeatureFlagsLocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun provideSettingsLocalRepository(
    @FeatureFlagsDataStore featureFlags: DataStore<Preferences>
  ) : FeatureFlagsLocalRepository =
    SettingsLocalRepositoryImpl(
      featureFlagsDataStore = featureFlags
    )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FeatureFlagsDataStore
