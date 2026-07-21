package cm.aptoide.pt.download_view.di

import cm.aptoide.pt.download_view.presentation.InlineInstallResolver
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface InlineInstallResolverModule {

  @BindsOptionalOf
  fun inlineInstallResolver(): InlineInstallResolver
}
