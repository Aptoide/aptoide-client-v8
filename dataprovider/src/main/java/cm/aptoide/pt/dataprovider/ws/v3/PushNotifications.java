package cm.aptoide.pt.dataprovider.ws.v3;

import java.util.Map;

import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by trinkes on 7/13/16.
 */
public abstract class PushNotifications<U> extends WebService<PushNotifications.Interfaces,U> {

	private static final String BASE_URL = "http://webservices.aptoide.com/webservices/3/";

	protected PushNotifications() {
		super(Interfaces.class, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), BASE_URL);
	}

	interface Interfaces {

		@POST("getPushNotifications")
		@FormUrlEncoded
		Observable<GetPushNotificationsResponse> getPushNotifications(@FieldMap Map<String,String> arg);
	}
}
