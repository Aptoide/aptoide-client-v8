package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class MarkAsReadRequest extends V7<BaseV7Response, MarkAsReadRequest.Body> {
  public MarkAsReadRequest(Body body, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
        + "/api/7.20181019/";
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setRead(body, bypassCache);
  }

  public static class Body extends BaseBody {
    private boolean cardDismiss;
    private String cardId;

    public Body(String cardId, boolean cardDismiss) {
      this.cardId = cardId;
      this.cardDismiss = cardDismiss;
    }

    public String getCardId() {
      return cardId;
    }

    public void setCardId(String cardId) {
      this.cardId = cardId;
    }

    public boolean isCardDismiss() {
      return cardDismiss;
    }

    public void setCardDismiss(boolean cardDismiss) {
      this.cardDismiss = cardDismiss;
    }
  }
}
