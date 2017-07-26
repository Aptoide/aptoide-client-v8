/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 30/06/2016.
 */

package cm.aptoide.pt.database.exceptions;

import cm.aptoide.pt.utils.BaseException;

/**
 * Created by marcelobenites on 6/30/16.
 */
public class DownloadNotFoundException extends BaseException {

  public DownloadNotFoundException() {
  }

  public DownloadNotFoundException(String msg) {
    super(msg);
  }
}
