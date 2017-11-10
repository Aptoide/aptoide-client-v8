/*
 * Copyright (c) 2016.
 * Modified on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v1.notification;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.model.v1.GetPullNotificationsResponse;
import cm.aptoide.pt.dataprovider.ws.v1.PnpV1WebService;
import cm.aptoide.pt.dataprovider.ws.v1.Service;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class PullSocialNotificationRequest
    extends PnpV1WebService<List<GetPullNotificationsResponse>> {
  private static List<Integer> pretendedNotificationTypes;
  private final Map<String, String> options;
  private final String id;

  private PullSocialNotificationRequest(String id, Map<String, String> options,
      OkHttpClient okHttpClient, Converter.Factory converterFactory) {
    super(okHttpClient, converterFactory);
    this.options = options;
    this.id = id;
  }

  public static PullSocialNotificationRequest of(String uniqueIdentifier, String versionName,
      String appId, OkHttpClient httpClient, Converter.Factory converterFactory, String oemid,
      SharedPreferences sharedPreferences, Resources resources, boolean isLogged) {

    Map<String, String> options = new HashMap<>();
    pretendedNotificationTypes = new ArrayList<>();

    options.put("language", AptoideUtils.SystemU.getCountryCode(resources));
    options.put("aptoide_version", versionName);

    if (isLogged) {
      pretendedNotificationTypes.add(1);
      pretendedNotificationTypes.add(2);
      pretendedNotificationTypes.add(4);
      pretendedNotificationTypes.add(5);
      pretendedNotificationTypes.add(6);
    }
    pretendedNotificationTypes.add(3);

    if (!TextUtils.isEmpty(oemid)) {
      options.put("oem_id", oemid);
    }
    options.put("aptoide_package", appId);
    if (ToolboxManager.isDebug(sharedPreferences)) {
      options.put("debug", "true");
    }
    options.put("status_in_json", String.valueOf(true));

    return new PullSocialNotificationRequest(uniqueIdentifier, options, httpClient,
        converterFactory);
  }

  @Override
  protected Observable<List<GetPullNotificationsResponse>> loadDataFromNetwork(Service interfaces,
      boolean bypassCache) {
    return interfaces.getPullSocialNotifications(true, id, pretendedNotificationTypes, options);
  }
}
