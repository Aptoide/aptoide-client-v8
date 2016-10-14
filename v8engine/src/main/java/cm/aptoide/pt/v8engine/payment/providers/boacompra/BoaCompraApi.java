/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 07/10/2016.
 */

package cm.aptoide.pt.v8engine.payment.providers.boacompra;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by marcelobenites on 10/7/16.
 */

interface BoaCompraApi {

  @POST("pre-approvals") Observable<PreApproval> createPreApproval(@Body PreApprovalRequest request);
}
