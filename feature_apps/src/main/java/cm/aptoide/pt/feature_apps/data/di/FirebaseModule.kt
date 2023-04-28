package cm.aptoide.pt.feature_apps.data.di

import com.google.firebase.installations.FirebaseInstallations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

  @Singleton
  @Provides
  fun provideFirebaseInstallations(): FirebaseInstallations =
    FirebaseInstallations.getInstance()
}
