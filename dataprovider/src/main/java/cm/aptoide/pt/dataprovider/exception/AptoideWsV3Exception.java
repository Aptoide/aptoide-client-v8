/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.dataprovider.exception;

import cm.aptoide.pt.dataprovider.ws.v3.GenericResponseV3;
import cm.aptoide.pt.utils.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Created by neuro on 19-05-2016.
 */
@EqualsAndHashCode(callSuper = false)
// FIXME Warning: Generating equals/hashCode implementation but without a call to superclass, even though this class does not extend java.lang.Object. If
// this is intentional, add '@EqualsAndHashCode(callSuper=false)' to your type.
@Data @Accessors(chain = true) public class AptoideWsV3Exception extends BaseException {

  private GenericResponseV3 baseResponse;

  public AptoideWsV3Exception(Throwable cause) {
    super(cause);
  }
}
