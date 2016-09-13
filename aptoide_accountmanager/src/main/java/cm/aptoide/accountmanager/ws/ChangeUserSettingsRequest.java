package cm.aptoide.accountmanager.ws;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.ws.responses.ChangeUserSettingsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 5/6/16.
 */
@Data @EqualsAndHashCode(callSuper = true) public class ChangeUserSettingsRequest
    extends v3accountManager<ChangeUserSettingsResponse> {

  public static final String ACCESS_TOKEN = "access_token";
  public static final String ACTIVE = "active";
  public static final String INACTIVE = "inactive";
  private ArrayList<String> list;
  private boolean matureSwitch;

  public ChangeUserSettingsRequest(OkHttpClient httpClient, Converter.Factory converterFactory) {
    super(httpClient, converterFactory);
    list = new ArrayList<>();
  }

  public static ChangeUserSettingsRequest of(boolean matureSwitchStatus) {
    ChangeUserSettingsRequest request =
        new ChangeUserSettingsRequest(OkHttpClientFactory.getSingletonClient(),
            WebService.getDefaultConverter());
    request.setMatureSwitch(matureSwitchStatus);
    return request;
  }

  public void changeMatureSwitchSetting() {
    list.add("matureswitch=" + (matureSwitch ? ACTIVE : INACTIVE));
  }

  @Override
  protected Observable<ChangeUserSettingsResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    HashMap<String, String> parameters = new HashMap<>();
    parameters.put("mode", "json");
    ArrayList<String> parametersList = setupParameters();
    parameters.put("settings", TextUtils.join(",", parametersList));

    if (AptoideAccountManager.getAccessToken() != null) {
      parameters.put(ACCESS_TOKEN, AptoideAccountManager.getAccessToken());
    }

    // TODO: 5/6/16 trinkes check if access_token is valid
    return interfaces.changeUserSettings(parameters);
  }

  private ArrayList<String> setupParameters() {
    changeMatureSwitchSetting();
    return list;
  }
}
