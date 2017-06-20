package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by jdandrade on 15/02/2017.
 */

public class SyncAddressBookRequest extends V7<GetFollowers, SyncAddressBookRequest.Body> {

  public SyncAddressBookRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @NonNull public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static SyncAddressBookRequest of(List<String> numbers, List<String> emails,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new SyncAddressBookRequest((new Body(new Contacts(numbers, emails), null, null)),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  /**
   * This constructor was created in order to send user twitter info
   */
  public static SyncAddressBookRequest of(long id, String token, String secret,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new SyncAddressBookRequest(new Body(null, new Twitter(id, token, secret), null),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  /**
   * This constructor was created to deal with facebook contacts request
   */
  public static SyncAddressBookRequest of(long id, String token,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    return new SyncAddressBookRequest(new Body(null, null, new Facebook(id, token)),
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setConnections(body);
  }

  public static class Body extends BaseBody implements Endless {
    private Contacts contacts;
    private Twitter twitter;
    private Facebook facebook;
    private int limit = 25;
    private int offset;

    public Body(Contacts contacts, Twitter twitter, Facebook facebook) {
      this.contacts = contacts;
      this.twitter = twitter;
      this.facebook = facebook;
    }

    public Contacts getContacts() {
      return contacts;
    }

    public void setContacts(Contacts contacts) {
      this.contacts = contacts;
    }

    public Twitter getTwitter() {
      return twitter;
    }

    public void setTwitter(Twitter twitter) {
      this.twitter = twitter;
    }

    public Facebook getFacebook() {
      return facebook;
    }

    public void setFacebook(Facebook facebook) {
      this.facebook = facebook;
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }

    public void setLimit(int limit) {
      this.limit = limit;
    }
  }

  private static class Contacts {
    private List<String> phones;
    private List<String> emails;

    public Contacts(List<String> phones, List<String> emails) {
      this.phones = phones;
      this.emails = emails;
    }

    public List<String> getPhones() {
      return phones;
    }

    public void setPhones(List<String> phones) {
      this.phones = phones;
    }

    public List<String> getEmails() {
      return emails;
    }

    public void setEmails(List<String> emails) {
      this.emails = emails;
    }
  }

  private static class Twitter {
    private Long id;
    private String token;
    private String secret;

    public Twitter(Long id, String token, String secret) {
      this.id = id;
      this.token = token;
      this.secret = secret;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public String getSecret() {
      return secret;
    }

    public void setSecret(String secret) {
      this.secret = secret;
    }
  }

  private static class Facebook {
    private Long id;
    private String token;

    public Facebook(Long id, String token) {
      this.id = id;
      this.token = token;
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }
  }
}
