/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 21/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.notifications;

import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import rx.Observable;

/**
 * Created by trinkes on 7/13/16.
 */
public class PullCampaignNotificationsRequest
    extends Notifications<List<GetPullNotificationsResponse>> {

  protected static String BASE_HOST = "http://pnp.aptoide.com/pnp/v1/notifications/";

  private final Map<String, String> options;
  private final String id;

  protected PullCampaignNotificationsRequest(String id, Map<String, String> options) {
    super(OkHttpClientFactory.getSingletonClient(() -> SecurePreferences.getUserAgent(), false),
        WebService.getDefaultConverter());
    this.options = options;
    this.id = id;
  }

  public static PullCampaignNotificationsRequest of(AptoideClientUUID aptoideClientUuid,
      String versionName, String appId) {

    Random r = new Random();
    int i1 = r.nextInt(100);

    //String id = aptoideClientUuid.getUniqueIdentifier() + "_" + i1;
    String id = aptoideClientUuid.getUniqueIdentifier();

    Map<String, String> options = new HashMap<String, String>();

    // language (default = ‘en’)
    // aptoide_version, e.g. 8.1.2.0
    // oem_id
    // aptoide_package, e.g. cm.aptoide.pt

    options.put("language", AptoideUtils.SystemU.getCountryCode());
    options.put("aptoide_version", versionName);
    String oemid = DataProvider.getConfiguration().getExtraId();
    if (!TextUtils.isEmpty(oemid)) {
      options.put("oem_id", oemid);
    }
    options.put("aptoide_package", appId);
    //TODO should depend of build variant
    options.put("debug", "true");

    return new PullCampaignNotificationsRequest(id, options);
  }

  @Override protected Observable<List<GetPullNotificationsResponse>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    Observable<List<GetPullNotificationsResponse>> pushNotificationsAmazon =
        interfaces.getPullCompaignNotifications(id, options, true);
    return pushNotificationsAmazon;
  }
}
