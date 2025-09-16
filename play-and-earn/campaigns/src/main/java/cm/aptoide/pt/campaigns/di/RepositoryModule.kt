package cm.aptoide.pt.campaigns.di

import cm.aptoide.pt.aptoide_network.di.ApiChainCatappultDomain
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.campaigns.data.DefaultPaECampaignsRepository
import cm.aptoide.pt.campaigns.data.PaECampaignsApi
import cm.aptoide.pt.campaigns.data.PaECampaignsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providePaECampaignsApi(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @ApiChainCatappultDomain apiChainCatappultDomain: String,
  ): PaECampaignsApi {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(apiChainCatappultDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PaECampaignsApi::class.java)
  }

  @Provides
  @Singleton
  fun providePaECampaignsRepository(
    paeCampaignsApi: PaECampaignsApi,
  ): PaECampaignsRepository = DefaultPaECampaignsRepository(
    paeCampaignsApi = paeCampaignsApi,
    dispatcher = Dispatchers.IO
  )
}
