package cm.aptoide.pt.download_view.di

import android.content.Context
import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

  @Singleton
  @Provides
  fun provideInstalledAppOpener(@ApplicationContext context: Context): InstalledAppOpener {
    return InstalledAppOpener(context)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UIInstallPackageInfoMapper
