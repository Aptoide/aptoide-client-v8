package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Returns the user timeline. A series of cards with information related to application releases, news, application
 * updates and so on.
 */
public class GetUserTimelineRequest extends V7<GetUserTimeline, GetUserTimelineRequest.Body> {

	public GetUserTimelineRequest(boolean bypassCache, Body body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(bypassCache, body, httpClient, converterFactory, baseHost);
	}

	@Override
	protected Observable<GetUserTimeline> loadDataFromNetwork(Interfaces interfaces) {
		return interfaces.getUserTimeline(body, bypassCache);
	}

	public static GetUserTimelineRequest of(boolean bypassCache) {
		GetUserTimelineRequest getAppRequest = new GetUserTimelineRequest(bypassCache,
				new Body("1", AptoideAccountManager.getAccessToken(),
						AptoideUtils.Core.getVerCode(), "pool", Api.LANG, Api.Q),
				OkHttpClientFactory.newClient(),
				WebService.getDefaultConverter(), BASE_HOST);
		return getAppRequest;
	}

	@Data
	@Accessors(chain = true)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements OffsetInterface<Body> {

		private String lang;
		private Integer limit;
		private boolean mature;
		private int offset;
		private String q;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, String q) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
			this.lang = lang;
			this.q = q;
		}
	}
}
