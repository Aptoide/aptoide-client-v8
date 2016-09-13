package cm.aptoide.accountmanager.ws;

import android.support.annotation.StringRes;
import cm.aptoide.accountmanager.R;
import cm.aptoide.pt.preferences.Application;

/**
 * Created by j-pac on 19-05-2014.
 */

public class ErrorsMapper {

  public static @StringRes int getWebServiceErrorMessageFromCode(String errorCode) {
    int error = Application.getContext()
        .getResources()
        .getIdentifier("ws_error_" + errorCode.replace("-", "_"), "string",
            Application.getContext().getPackageName());

    return error == 0 ? R.string.unknown_error : error;
  }
}
