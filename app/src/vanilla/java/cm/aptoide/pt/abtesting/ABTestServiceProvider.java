package cm.aptoide.pt.abtesting;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ABTestServiceProvider {

  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private CallAdapter.Factory rxCallAdapterFactory;
  private SharedPreferences sharedPreferences;

  public ABTestServiceProvider(OkHttpClient httpClient, Converter.Factory converterFactory,
      CallAdapter.Factory rxCallAdapterFactory, SharedPreferences sharedPreferences) {
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.rxCallAdapterFactory = rxCallAdapterFactory;
    this.sharedPreferences = sharedPreferences;
  }

  public ABTestService.ABTestingService getService(BaseExperiment.ExperimentType type) {
    if (type.equals(BaseExperiment.ExperimentType.RAKAM)) {
      return getABTestService(
          decorateWithSchemeAndAPI(BuildConfig.APTOIDE_WEB_SERVICES_AB_TEST_HOST));
    } else if (type.equals(BaseExperiment.ExperimentType.WASABI)) {
      return getABTestService(
          decorateWithSchemeAndAPI(BuildConfig.APTOIDE_WEB_SERVICES_AB_TESTING_HOST));
    }
    throw new IllegalStateException(
        "You need to pass a valid ExperimentType! All experiments must be assigned to an Experiment type so that the base host can be correctly assigned");
  }

  @NotNull private String decorateWithSchemeAndAPI(String host) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : cm.aptoide.pt.dataprovider.BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + host
        + "/api/v1/";
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
