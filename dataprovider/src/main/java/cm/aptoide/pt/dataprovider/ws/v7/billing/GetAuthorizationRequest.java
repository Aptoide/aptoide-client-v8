package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class GetAuthorizationRequest
    extends V7<Response<GetAuthorizationRequest.ResponseBody>, BaseBody> {

  private final long transactionId;
  private final String accessToken;
  private final String customerId;

  private GetAuthorizationRequest(String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, long transactionId, String accessToken,
      String customerId) {
    super(new BaseBody(), baseHost, httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    this.transactionId = transactionId;
    this.accessToken = accessToken;
    this.customerId = customerId;
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static GetAuthorizationRequest of(long transactionId, SharedPreferences sharedPreferences,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      String accessToken, String customerId) {
    return new GetAuthorizationRequest(getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, transactionId, accessToken,
        customerId);
  }

  @Override
  protected Observable<Response<GetAuthorizationRequest.ResponseBody>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.getBillingAuthorization(transactionId, "Bearer " + accessToken, customerId);
  }

  public static class ResponseBody extends BaseV7Response {

    private Authorization data;

    public Authorization getData() {
      return data;
    }

    public void setData(Authorization data) {
      this.data = data;
    }

    public static class Authorization {

      private long id;
      private String type;
      private long serviceId;
      private Price price;
      private User user;
      private String status;
      private Metadata data;

      public long getId() {
        return id;
      }

      public void setId(long id) {
        this.id = id;
      }

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public long getServiceId() {
        return serviceId;
      }

      public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
      }

      public Price getPrice() {
        return price;
      }

      public void setPrice(Price price) {
        this.price = price;
      }

      public User getUser() {
        return user;
      }

      public void setUser(User user) {
        this.user = user;
      }

      public String getStatus() {
        return status;
      }

      public void setStatus(String status) {
        this.status = status;
      }

      public Metadata getData() {
        return data;
      }

      public void setData(Metadata data) {
        this.data = data;
      }

      public static class User {

        private long id;

        public long getId() {
          return id;
        }

        public void setId(long id) {
          this.id = id;
        }
      }

      public static class Metadata {

        private String url;
        private String redirectUrl;
        private String description;
        private String session;

        public String getUrl() {
          return url;
        }

        public void setUrl(String url) {
          this.url = url;
        }

        public String getRedirectUrl() {
          return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
          this.redirectUrl = redirectUrl;
        }

        public String getDescription() {
          return description;
        }

        public void setDescription(String description) {
          this.description = description;
        }

        public String getSession() {
          return session;
        }

        public void setSession(String session) {
          this.session = session;
        }
      }

      public static class Price {

        private double amount;
        private String currency;
        private String currencySymbol;

        public double getAmount() {
          return amount;
        }

        public void setAmount(double amount) {
          this.amount = amount;
        }

        public String getCurrency() {
          return currency;
        }

        public void setCurrency(String currency) {
          this.currency = currency;
        }

        public String getCurrencySymbol() {
          return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
          this.currencySymbol = currencySymbol;
        }
      }
    }
  }
}
