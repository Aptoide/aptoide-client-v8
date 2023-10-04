package cm.aptoide.pt.oem_extractor.di

import cm.aptoide.pt.oem_extractor.cache.ExtractorCache
import com.aptoide.apk.injector.extractor.IExtractorCache
import com.aptoide.apk.injector.extractor.data.Extractor
import com.aptoide.apk.injector.extractor.data.ExtractorV1
import com.aptoide.apk.injector.extractor.data.ExtractorV2
import com.aptoide.apk.injector.extractor.domain.IExtract
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface LibExtractor {

  @Singleton
  @Binds
  fun bindIExtractorCache(extractor: ExtractorCache): IExtractorCache

  companion object {
    @Singleton
    @Provides
    @JvmStatic
    fun provideExtractor(): IExtract {
      return Extractor(ExtractorV1(), ExtractorV2())
    }
  }
}
