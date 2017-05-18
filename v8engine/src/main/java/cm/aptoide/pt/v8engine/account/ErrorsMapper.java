/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.pt.v8engine.account;

import android.support.annotation.StringRes;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by j-pac on 19-05-2014.
 */

public class ErrorsMapper {

  public static @StringRes int getWebServiceErrorMessageFromCode(String errorCode) {
    int error = Application.getContext()
        .getResources()
        .getIdentifier("ws_error_" + errorCode.replace("-", "_"), "string", Application.getContext()
            .getPackageName());

    return error == 0 ? R.string.unknown_error : error;
  }
}
