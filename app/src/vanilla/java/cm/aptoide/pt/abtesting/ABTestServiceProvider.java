package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.dataprovider.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ABTestServiceProvider {

  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private CallAdapter.Factory rxCallAdapterFactory;

  public ABTestServiceProvider(OkHttpClient httpClient, Converter.Factory converterFactory,
      CallAdapter.Factory rxCallAdapterFactory) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.rxCallAdapterFactory = rxCallAdapterFactory;
  }

  public ABTestService.ABTestingService getService(BaseExperiment.ExperimentType type) {
    if (type.equals(BaseExperiment.ExperimentType.RAKAM)) {
      return getABTestService(BuildConfig.APTOIDE_WEB_SERVICES_AB_TEST_HOST);
    } else if (type.equals(BaseExperiment.ExperimentType.WASABI)) {
      return getABTestService(BuildConfig.APTOIDE_WEB_SERVICES_AB_TESTING_HOST);
    }
    throw new IllegalStateException(
        "You need to pass a valid ExperimentType! All experiments must be assigned to an Experiment type so that the base host can be correctly assigned");
  }

  private ABTestService.ABTestingService getABTestService(String baseHost) {
    return createRetrofit(baseHost).create(ABTestService.ABTestingService.class);
  }

  private Retrofit createRetrofit(String baseHost) {
    return new Retrofit.Builder().baseUrl(baseHost)
        .client(httpClient)
        .addCallAdapterFactory(rxCallAdapterFactory)
        .addConverterFactory(converterFactory)
        .build();
  }
}
