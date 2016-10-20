/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by marcelobenites on 10/7/16.
 */

interface BoaCompraApi {

  @POST("pre-approvals") Observable<PreApprovalAuthorizationResponse> createPreApproval(
      @Body PreApprovalAuthorizationRequest request);

  @GET("pre-approvals/{code}") Observable<PreApprovalListResponse> getPreApproval(
      @Path("code") String code);
}