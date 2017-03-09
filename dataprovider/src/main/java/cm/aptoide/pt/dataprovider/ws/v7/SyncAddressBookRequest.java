package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.model.v7.GetFollowers;
import java.util.List;
import lombok.Data;
import rx.Observable;

/**
 * Created by jdandrade on 15/02/2017.
 */

public class SyncAddressBookRequest extends V7<GetFollowers, SyncAddressBookRequest.Body> {

  private static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
      + "/api/7/";

  public SyncAddressBookRequest(Body body, String baseHost) {
    super(body, baseHost);
  }

  public static SyncAddressBookRequest of(List<String> numbers, List<String> emails, BodyInterceptor bodyInterceptor) {

    return new SyncAddressBookRequest(((SyncAddressBookRequest.Body) bodyInterceptor.intercept(
        new Body(new Contacts(numbers, emails), null, null))), BASE_HOST);
  }

  /**
   * This constructor was created in order to send user twitter info
   *
   * @param id
   * @param token
   * @param secret
   * @param bodyInterceptor
   * @return
   */
  public static SyncAddressBookRequest of(long id, String token, String secret,
      BodyInterceptor bodyInterceptor) {

    return new SyncAddressBookRequest(((SyncAddressBookRequest.Body) bodyInterceptor.intercept(
        new Body(null, new Twitter(id, token, secret), null))), BASE_HOST);
  }

  /**
   * This constructor was created to deal with facebook contacts request
   */
  public static SyncAddressBookRequest of(long id, String token, BodyInterceptor bodyInterceptor) {

    return new SyncAddressBookRequest(((SyncAddressBookRequest.Body) bodyInterceptor.intercept(
        new Body(null, null, new Facebook(id, token)))), BASE_HOST);
  }

  @Override protected Observable<GetFollowers> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.setConnections(body);
  }

  @Data public static class Body extends BaseBody implements Endless {
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

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }
  }

  @Data private static class Contacts {
    private List<String> phones;
    private List<String> emails;

    public Contacts(List<String> phones, List<String> emails) {
      this.phones = phones;
      this.emails = emails;
    }
  }

  @Data private static class Twitter {
    private Long id;
    private String token;
    private String secret;

    public Twitter(Long id, String token, String secret) {
      this.id = id;
      this.token = token;
      this.secret = secret;
    }
  }

  @Data private static class Facebook {
    private Long id;
    private String token;

    public Facebook(Long id, String token) {
      this.id = id;
      this.token = token;
    }
  }
}
