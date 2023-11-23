package cm.aptoide.pt.feature_updates.di

import android.content.Context
import cm.aptoide.pt.feature_updates.presentation.InstalledAppOpener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

  @Provides
  fun provideInstalledAppOpener(@ApplicationContext context: Context): InstalledAppOpener {
    return InstalledAppOpener(context)
  }
}
