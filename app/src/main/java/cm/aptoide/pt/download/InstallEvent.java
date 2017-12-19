package cm.aptoide.pt.download;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 05/01/2017.
 */

public class InstallEvent extends DownloadInstallBaseEvent {
  private static final String EVENT_NAME = "INSTALL";
  private boolean aptoideSettings;
  private boolean isPhoneRooted;

  public InstallEvent(Action action, Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, AppContext context, int versionCode,
      DownloadInstallEventConverter downloadInstallEventConverter,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, String previousContext, String screenHistoryStore,
      String screenHistoryTag) {
    super(action, origin, packageName, url, obbUrl, patchObbUrl, context, versionCode,
        downloadInstallEventConverter, EVENT_NAME, bodyInterceptor, httpClient, converterFactory,
        tokenInvalidator, sharedPreferences, previousContext, screenHistoryStore, screenHistoryTag);
  }

  public void setPhoneRooted(boolean phoneRooted) {
    isPhoneRooted = phoneRooted;
  }

  public boolean getAptoideSettings() {
    return aptoideSettings;
  }

  public void setAptoideSettings(boolean aptoideSettings) {
    this.aptoideSettings = aptoideSettings;
  }

  public boolean getIsPhoneRooted() {
    return isPhoneRooted;
  }
}
