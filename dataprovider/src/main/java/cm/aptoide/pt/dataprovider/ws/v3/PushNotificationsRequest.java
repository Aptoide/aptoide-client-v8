package cm.aptoide.pt.dataprovider.ws.v3;

import android.text.TextUtils;

import java.util.HashMap;

import cm.aptoide.accountmanager.BuildConfig;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import rx.Observable;

/**
 * Created by trinkes on 7/13/16.
 */
public class PushNotificationsRequest extends PushNotifications<GetPushNotificationsResponse> {

	public static PushNotificationsRequest of() {
		return new PushNotificationsRequest();
	}

	@Override
	protected Observable<GetPushNotificationsResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		HashMap<String,String> parameters = new HashMap<String,String>();

		String oemid = DataProvider.getConfiguration().getExtraId();
		if (!TextUtils.isEmpty(oemid)) {
			parameters.put("oem_id", oemid);
		}
		parameters.put("mode", "json");
		parameters.put("limit", "1");
		parameters.put("lang", AptoideUtils.SystemU.getCountryCode());

		// TODO: 7/13/16 trinkes verify this to work with aptoide toolbox
		if (BuildConfig.DEBUG) {
			parameters.put("notification_type", "aptoide_tests");
		} else {
			parameters.put("notification_type", "aptoide_vanilla");
		}
		parameters.put("id", String.valueOf(ManagerPreferences.getLastPushNotificationId()));
		return interfaces.getPushNotifications(parameters);
	}
}
