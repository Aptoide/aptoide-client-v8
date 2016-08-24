/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 20/07/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by sithengineer on 20/07/16.
 */
public class PostCommentRequest extends V7<BaseV7Response,PostCommentRequest.Body> {

	private static final String BASE_HOST = "http://ws75-primary.aptoide.com/api/7/";
	
	protected PostCommentRequest(Body body, String baseHost) {
		super(body, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	public static PostCommentRequest of(long reviewId, String text) {
		//
		//  http://ws75-primary.aptoide.com/api/7/setComment/review_id/1/body/amazing%20review/access_token/ca01ee1e05ab4d82d99ef143e2816e667333c6ef
		//
		BaseBodyDecorator decorator = new BaseBodyDecorator(new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()),SecurePreferencesImplementation.getInstance());
		IdsRepository idsRepository = new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext());
		Body body = new Body(reviewId, text);
		return new PostCommentRequest((Body) decorator.decorate(body), BASE_HOST);
	}

	@Override
	protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {
		return interfaces.postComment(body, true);
	}

	@Data
	@Accessors(chain = false)
	@EqualsAndHashCode(callSuper = true)
	public static class Body extends BaseBody {

		private long reviewId;
		private String body;

		public Body(long reviewId, String
				text) {
			this.reviewId = reviewId;
			this.body = text;
		}
	}
}
