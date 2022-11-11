package cm.aptoide.pt.download_view.di

import android.content.Context
import cm.aptoide.pt.download_view.presentation.InstalledAppOpener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class UseCaseModule {

  @Provides
  fun provideInstalledAppOpener(@ApplicationContext context: Context): InstalledAppOpener {
    return InstalledAppOpener(context)
  }
}
