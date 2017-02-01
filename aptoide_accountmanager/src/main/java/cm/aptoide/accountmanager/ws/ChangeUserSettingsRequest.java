package cm.aptoide.accountmanager.ws;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.ChangeUserSettingsResponse;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import java.util.ArrayList;
import lombok.Data;
import lombok.EqualsAndHashCode;
import rx.Observable;

/**
 * Created by trinkes on 5/6/16.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ChangeUserSettingsRequest
    extends v3accountManager<ChangeUserSettingsResponse> {

  public static final String ACCESS_TOKEN = "access_token";
  public static final String ACTIVE = "active";
  public static final String INACTIVE = "inactive";

  private final ArrayList<String> list;
  private boolean matureSwitch;

  private ChangeUserSettingsRequest() {
    list = new ArrayList<>();
  }

  public static ChangeUserSettingsRequest of(boolean matureSwitchStatus) {
    ChangeUserSettingsRequest request = new ChangeUserSettingsRequest();
    request.setMatureSwitch(matureSwitchStatus);
    return request;
  }

  @Override
  protected Observable<ChangeUserSettingsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    HashMapNotNull<String, String> parameters = new HashMapNotNull<>();
    parameters.put("mode", "json");
    ArrayList<String> parametersList = setupParameters();
    parameters.put("settings", TextUtils.join(",", parametersList));

    final String accessToken = AptoideAccountManager.getAccessToken();
    if (TextUtils.isEmpty(accessToken)) {
      parameters.put(ACCESS_TOKEN, accessToken);
    }

    return interfaces.changeUserSettings(parameters);
  }

  private ArrayList<String> setupParameters() {
    list.add("matureswitch=" + (matureSwitch ? ACTIVE : INACTIVE));
    return list;
  }
}
