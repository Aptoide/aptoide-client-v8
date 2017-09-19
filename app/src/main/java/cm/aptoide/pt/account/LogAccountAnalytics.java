package cm.aptoide.pt.account;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AccountAnalytics;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.events.AptoideEvent;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.logger.Logger;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 22/05/2017.
 */
public class LogAccountAnalytics implements AccountAnalytics {
  private static final String TAG = LogAccountAnalytics.class.getSimpleName();
  private final Analytics analytics;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final TokenInvalidator tokenInvalidator;
  private final String appId;
  private final SharedPreferences sharedPreferences;

  public LogAccountAnalytics(Analytics analytics, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, String appId, SharedPreferences sharedPreferences) {
    this.analytics = analytics;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.tokenInvalidator = tokenInvalidator;
    this.appId = appId;
    this.sharedPreferences = sharedPreferences;
  }

  @Override public void login(String email) {
    analytics.sendEvent(
        new AptoideEvent(null, "LOGIN", "CLICK", "LOGIN", bodyInterceptor, httpClient,
            converterFactory, tokenInvalidator, appId, sharedPreferences));
    Logger.d(TAG, "login() called with: " + "email = [" + email + "]");
  }

  @Override public void signUp() {
    Logger.d(TAG, "signUp() called");
  }
}
