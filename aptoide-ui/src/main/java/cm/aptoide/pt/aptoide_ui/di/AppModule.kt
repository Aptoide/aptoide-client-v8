package cm.aptoide.pt.aptoide_ui.di

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class AppModule {

  @Provides
  @ViewModelScoped
  fun provideVideoPlayer(@ApplicationContext context: Context): Player {
    return ExoPlayer.Builder(context).build()
  }
}