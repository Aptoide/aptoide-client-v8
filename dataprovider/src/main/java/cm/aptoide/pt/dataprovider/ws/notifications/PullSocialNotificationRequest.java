/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.notifications;

import android.text.TextUtils;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 7/13/16.
 */
public class PullSocialNotificationRequest
    extends Notifications<List<GetPullNotificationsResponse>> {

  protected static String BASE_HOST = "http://pnp.aptoide.com/pnp/v1/notifications/";
  private static List<Integer> pretendedNotificationTypes;
  private final Map<String, String> options;
  private final String id;

  protected PullSocialNotificationRequest(String id, Map<String, String> options,
      OkHttpClient okHttpClient, Converter.Factory converterFactory) {
    super(okHttpClient, converterFactory);
    this.options = options;
    this.id = id;
  }

  public static PullSocialNotificationRequest of(String uniqueIdentifier, String versionName,
      String appId, OkHttpClient httpClient, Converter.Factory converterFactory, String oemid,
      String accessToken) {

    Map<String, String> options = new HashMap<>();
    pretendedNotificationTypes = new ArrayList<>();

    options.put("language", AptoideUtils.SystemU.getCountryCode());
    options.put("aptoide_version", versionName);

    if (!TextUtils.isEmpty(accessToken)) {
      options.put("access_token", accessToken);
      pretendedNotificationTypes.add(1);
      pretendedNotificationTypes.add(2);
    }
    pretendedNotificationTypes.add(3);

    if (!TextUtils.isEmpty(oemid)) {
      options.put("oem_id", oemid);
    }
    options.put("aptoide_package", appId);
    if (ManagerPreferences.isDebug()) {
      options.put("debug", "true");
    }

    return new PullSocialNotificationRequest(uniqueIdentifier, options, httpClient,
        converterFactory);
  }

  @Override protected Observable<List<GetPullNotificationsResponse>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.getPullSocialNotifications(true, id, pretendedNotificationTypes, options);
  }
}
