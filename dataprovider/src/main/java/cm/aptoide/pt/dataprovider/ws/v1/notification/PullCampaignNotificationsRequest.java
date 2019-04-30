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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class PullCampaignNotificationsRequest
    extends PnpV1WebService<List<GetPullNotificationsResponse>> {

  private final Map<String, String> options;
  private final String id;

  protected PullCampaignNotificationsRequest(String id, Map<String, String> options,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      SharedPreferences sharedPreferences) {
    super(httpClient, converterFactory, sharedPreferences);
    this.options = options;
    this.id = id;
  }

  public static PullCampaignNotificationsRequest of(String aptoideClientUuid, String versionName,
      String appId, OkHttpClient httpClient, Converter.Factory converterFactory, String extraId,
      SharedPreferences sharedPreferences, Resources resources) {

    Map<String, String> options = new HashMap<>();

    options.put("language", AptoideUtils.SystemU.getCountryCode(resources));
    options.put("aptoide_version", versionName);
    String oemid = extraId;
    if (!TextUtils.isEmpty(oemid)) {
      options.put("oem_id", oemid);
    }
    options.put("aptoide_package", appId);
    if (ToolboxManager.isDebug(sharedPreferences)) {
      options.put("debug", "true");
    }

    return new PullCampaignNotificationsRequest(aptoideClientUuid, options, httpClient,
        converterFactory, sharedPreferences);
  }

  @Override
  protected Observable<List<GetPullNotificationsResponse>> loadDataFromNetwork(Service interfaces,
      boolean bypassCache) {
    return interfaces.getPullCampaignNotifications(id, options, true);
  }
}
