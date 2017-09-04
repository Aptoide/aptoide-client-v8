package cm.aptoide.pt.view.account.user;

import android.content.Context;
import android.content.res.Resources;
import cm.aptoide.accountmanager.AccountValidationException;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.ThrowableToStringMapper;
import cm.aptoide.pt.view.account.AccountErrorMapper;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * Created by marcelobenites on 10/03/17.
 */

public class CreateUserErrorMapper implements ThrowableToStringMapper {

  private final Context context;
  private final AccountErrorMapper accountErrorMapper;
  private Resources resources;

  public CreateUserErrorMapper(Context context, AccountErrorMapper accountErrorMapper,
      Resources resources) {
    this.context = context;
    this.accountErrorMapper = accountErrorMapper;
    this.resources = resources;
  }

  @Override public String map(Throwable throwable) {
    String message = accountErrorMapper.map(throwable);

    if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
      message = context.getString(R.string.user_upload_photo_failed);
    } else if (throwable instanceof AccountValidationException) {
      switch (((AccountValidationException) throwable).getCode()) {
        case AccountValidationException.EMPTY_NAME:
          message = AptoideUtils.StringU.getResString(R.string.no_username_inserted, resources);
          break;
        case AccountValidationException.EMPTY_NAME_AND_AVATAR:
          message = AptoideUtils.StringU.getResString(R.string.nothing_inserted_user, resources);
          break;
      }
    }
    return message;
  }
}
