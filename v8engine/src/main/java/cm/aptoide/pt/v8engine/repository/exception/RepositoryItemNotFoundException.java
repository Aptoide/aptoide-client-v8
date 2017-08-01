/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.repository.exception;

import cm.aptoide.pt.utils.BaseException;

public class RepositoryItemNotFoundException extends BaseException {

  public RepositoryItemNotFoundException(String detailMessage) {
    super(detailMessage);
  }
}
