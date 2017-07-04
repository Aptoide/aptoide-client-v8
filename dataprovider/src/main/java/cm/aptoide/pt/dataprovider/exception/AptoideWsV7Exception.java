/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.dataprovider.exception;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.utils.BaseException;

/**
 * Created by neuro on 19-05-2016.
 */
public class AptoideWsV7Exception extends BaseException {

  private BaseV7Response baseResponse;

  public AptoideWsV7Exception(Throwable cause) {
    super(cause);
  }

  public BaseV7Response getBaseResponse() {
    return baseResponse;
  }

  public void setBaseResponse(BaseV7Response baseResponse) {
    this.baseResponse = baseResponse;
  }
}
