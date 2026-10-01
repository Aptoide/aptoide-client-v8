package cm.aptoide.pt.feature_categories.di

import cm.aptoide.pt.aptoide_network.di.BackendOverride
import cm.aptoide.pt.feature_categories.data.CategoriesRepository
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * The repositories a build may replace. One that binds nothing here keeps the v7
 * implementations - see [RepositoryModule].
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface BackendOverrideModule {

  @BindsOptionalOf
  @BackendOverride
  fun categoriesRepository(): CategoriesRepository
}
