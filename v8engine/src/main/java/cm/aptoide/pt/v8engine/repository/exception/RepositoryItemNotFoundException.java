/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.repository.exception;

import cm.aptoide.pt.utils.BaseException;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class RepositoryItemNotFoundException extends BaseException {

  public RepositoryItemNotFoundException(String detailMessage) {
    super(detailMessage);
  }

  public RepositoryItemNotFoundException() {

  }
}
