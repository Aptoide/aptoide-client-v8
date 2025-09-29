package cm.aptoide.pt.usage_stats.di

import android.content.Context
import cm.aptoide.pt.usage_stats.DefaultPackageUsageManager
import cm.aptoide.pt.usage_stats.PackageUsageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object UsageStatsModule {

  @Provides
  @Singleton
  fun providePackageUsageManager(
    @ApplicationContext context: Context
  ): PackageUsageManager {
    return DefaultPackageUsageManager(context)
  }
}
