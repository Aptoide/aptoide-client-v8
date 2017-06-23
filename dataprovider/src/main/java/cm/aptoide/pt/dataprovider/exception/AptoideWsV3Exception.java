/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.exception;

import cm.aptoide.pt.dataprovider.ws.v3.GenericResponseV3;
import cm.aptoide.pt.utils.BaseException;

/**
 * Created by neuro on 19-05-2016.
 */
public class AptoideWsV3Exception extends BaseException {

  private GenericResponseV3 baseResponse;

  public AptoideWsV3Exception(Throwable cause) {
    super(cause);
  }

  public GenericResponseV3 getBaseResponse() {
    return baseResponse;
  }

  public void setBaseResponse(GenericResponseV3 baseResponse) {
    this.baseResponse = baseResponse;
  }
}
