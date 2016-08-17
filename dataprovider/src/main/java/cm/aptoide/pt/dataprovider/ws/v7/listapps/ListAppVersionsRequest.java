/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.listapps;

import java.util.List;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppVersionsRequest extends V7<ListAppVersions,ListAppVersionsRequest.Body> {

	private static final Integer MAX_LIMIT = 10;

	private ListAppVersionsRequest(OkHttpClient httpClient, Converter.Factory converterFactory, Body body, String baseHost) {
		super(body, httpClient, converterFactory, baseHost);
	}

	public static ListAppVersionsRequest of() {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		Body body = new Body();
		body.setLimit(MAX_LIMIT);
		return new ListAppVersionsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(body),
				BASE_HOST);
	}

	public static ListAppVersionsRequest of(int limit, int offset) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),
				SecurePreferencesImplementation

				.getInstance());
		Body body = new Body();
		body.setLimit(limit);
		body.setOffset(offset);
		return new ListAppVersionsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(body),
				BASE_HOST);
	}

	public static ListAppVersionsRequest of(String packageName) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		Body body = new Body(packageName);
		body.setLimit(MAX_LIMIT);
		return new ListAppVersionsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(body),
				BASE_HOST);
	}

	public static ListAppVersionsRequest of(String packageName, int limit, int offset) {
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),
				SecurePreferencesImplementation
				.getInstance());
		Body body = new Body(packageName);
		body.setLimit(limit);
		body.setOffset(offset);
		return new ListAppVersionsRequest(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), (Body) decorator.decorate(body), BASE_HOST);
	}

	@Override
	protected Observable<ListAppVersions> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.listAppVersions(body, bypassCache);
	}

	@Data
	@Accessors(chain = false)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody implements Endless {

		private Integer apkId;
		private String apkMd5sum;
		private Integer appId;
		private String lang = Api.LANG;
		@Setter @Getter private Integer limit;
		@Setter @Getter private int offset;
		private Integer packageId;
		private String packageName;
		private String q = Api.Q;
		private List<Long> storeIds;
		private List<String> storeNames;

		public Body() {
		}

		public Body(String packageName) {
			this.packageName = packageName;
		}
	}
}
