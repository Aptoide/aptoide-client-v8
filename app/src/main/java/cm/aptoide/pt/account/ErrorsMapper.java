/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 10/02/2017.
 */

package cm.aptoide.pt.account;

import android.content.res.Resources;
import android.support.annotation.StringRes;
import cm.aptoide.pt.R;

/**
 * Created by j-pac on 19-05-2014.
 */

public class ErrorsMapper {

  public @StringRes int getWebServiceErrorMessageFromCode(String errorCode, String packageName,
      Resources resources) {
    //as described here https://developer.android.com/studio/build/shrink-code.html#gradle-shrinker -> Enable strict reference checks
    //this method prevents shrink resource to remove these strings
    int error = resources.getIdentifier(String.format("ws_error_%1s", errorCode.replace("-", "_")),
        "string", packageName);
    return error == 0 ? R.string.unknown_error : error;
  }
}
