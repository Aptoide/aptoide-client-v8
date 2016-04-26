/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.model.v7.GetStoreResponse;
import cm.aptoide.pt.networkclient.WebService;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U> extends WebService<V7.Interfaces, U> {

	protected V7() {
		super(Interfaces.class);
	}

	@Override
	protected String getBaseHost() {
		return "http://ws75.aptoide.com/api/7/";
	}

	public interface Interfaces {

		@POST("getStore")
		Observable<GetStoreResponse> getStore(@Body GetStoreRequest.Body arg);
	}
}
