package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by diogoloureiro on 12/09/16.
 */
@EqualsAndHashCode(callSuper = true) public class BaseBodyWithApp extends BaseBodyWithAlphaBetaKey {
  @Getter @Setter private String storeUser;
  @Getter @Setter private String storePassSha1;

  public BaseBodyWithApp(SharedPreferences sharedPreferences) {
    super(sharedPreferences);
  }
}
