package cm.aptoide.pt.oem_extractor.di

import android.content.Context
import android.content.SharedPreferences
import cm.aptoide.pt.oem_extractor.BuildConfig
import cm.aptoide.pt.oem_extractor.OemIdExtractorImpl
import cm.aptoide.pt.oem_extractor.cache.ExtractorCache
import cm.aptoide.pt.osp_handler.handler.OemIdExtractor
import com.aptoide.apk.injector.extractor.IExtractorCache
import com.aptoide.apk.injector.extractor.data.Extractor
import com.aptoide.apk.injector.extractor.data.ExtractorV1
import com.aptoide.apk.injector.extractor.data.ExtractorV2
import com.aptoide.apk.injector.extractor.domain.IExtract
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ExtractorModule {

  @Singleton
  @Binds
  fun bindOemIdExtractorService(extractor: OemIdExtractorImpl): OemIdExtractor

  @Singleton
  @Binds
  fun bindIExtractorCache(extractor: ExtractorCache): IExtractorCache

  companion object {
    @Singleton
    @Provides
    @JvmStatic
    fun provideExtractor(): IExtract {
      return Extractor(
        ExtractorV1(),
        ExtractorV2()
      )
    }

    @Singleton
    @Provides
    @JvmStatic
    @OemIdSharedPreferences
    fun provideSharedPreferences(@ApplicationContext context: Context) : SharedPreferences =
      context.getSharedPreferences("${BuildConfig.LIBRARY_PACKAGE_NAME}.oem_id_prefs", Context.MODE_PRIVATE)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OemIdSharedPreferences
