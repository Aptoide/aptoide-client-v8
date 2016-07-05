package cm.aptoide.pt.dataprovider.ws.v7;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Returns the user timeline. A series of cards with information related to application releases, news, application
 * updates and so on.
 */
public class GetUserTimelineRequest extends V7<GetUserTimeline, GetUserTimelineRequest.Body> {

	public GetUserTimelineRequest(Body body, OkHttpClient httpClient, Converter.Factory converterFactory, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
	}

	@Override
	protected Observable<GetUserTimeline> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.getUserTimeline(body, bypassCache);
	}

	public static GetUserTimelineRequest of(int limit, int offset, List<String> packages) {
		GetUserTimelineRequest getAppRequest = new GetUserTimelineRequest(new Body(SecurePreferences.getAptoideClientUUID(),
				AptoideAccountManager.getAccessToken(), AptoideUtils.Core.getVerCode(), "pool", Api.LANG, limit,
				AptoideAccountManager.getUserInfo().isMatureSwitch(), offset, Api.Q, packages), OkHttpClientFactory.newClient() ,
				WebService.getDefaultConverter(), BASE_HOST);
		return getAppRequest;
	}

	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements OffsetInterface<Body> {

		@Getter private String lang;
		@Getter private Integer limit;
		@Getter private boolean mature;
		@Accessors(chain = true) @Setter @Getter private int offset;
		@Getter private String q;
		@JsonProperty("package_names") @Getter private List<String> installedPackages;

		public Body(String aptoideId, String accessToken, int aptoideVercode, String cdn, String lang, Integer limit,
		            boolean mature, int offset, String q, List<String> installedPackages) {
			super(aptoideId, accessToken, aptoideVercode, cdn);
			this.lang = lang;
			this.limit = limit;
			this.mature = mature;
			this.offset = offset;
			this.q = q;
			this.installedPackages = installedPackages;
		}
	}
}
