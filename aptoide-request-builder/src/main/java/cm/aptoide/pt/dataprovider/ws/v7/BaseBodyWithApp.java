package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;

/**
 * Created by diogoloureiro on 12/09/16.
 */
public class BaseBodyWithApp extends BaseBodyWithAlphaBetaKey {
  private String storeUser;
  private String storePassSha1;

  public String getStoreUser() {
    return storeUser;
  }

  public void setStoreUser(String storeUser) {
    this.storeUser = storeUser;
  }

  public String getStorePassSha1() {
    return storePassSha1;
  }

  public void setStorePassSha1(String storePassSha1) {
    this.storePassSha1 = storePassSha1;
  }

  public BaseBodyWithApp(SharedPreferences sharedPreferences) {
    super(sharedPreferences);
  }
}
