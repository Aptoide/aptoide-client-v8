/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 26/08/2016.
 */

package cm.aptoide.pt.v8engine.repository.exception;

import cm.aptoide.pt.utils.BaseException;

/**
 * Created by marcelobenites on 8/26/16.
 */
public class RepositoryIllegalArgumentException extends BaseException {

  public RepositoryIllegalArgumentException(String detailMessage) {
    super(detailMessage);
  }

  public RepositoryIllegalArgumentException() {

  }
}
