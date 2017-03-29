package cm.aptoide.pt.v8engine.view;

import android.content.Context;
import cm.aptoide.accountmanager.AccountValidationException;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

/**
 * Created by marcelobenites on 10/03/17.
 */

public class CreateUserErrorMapper implements ThrowableToStringMapper {

  private final Context context;
  private final AccountErrorMapper accountErrorMapper;

  public CreateUserErrorMapper(Context context, AccountErrorMapper accountErrorMapper) {
    this.context = context;
    this.accountErrorMapper = accountErrorMapper;
  }

  @Override public String map(Throwable throwable) {
    String message = accountErrorMapper.map(throwable);

    if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
      message = context.getString(cm.aptoide.accountmanager.R.string.user_upload_photo_failed);
    } else if (throwable instanceof AccountValidationException) {
      switch (((AccountValidationException) throwable).getCode()) {
        case AccountValidationException.EMPTY_NAME:
          message = AptoideUtils.StringU.getResString(R.string.no_username_inserted);
          break;
        case AccountValidationException.EMPTY_NAME_AND_AVATAR:
          message = AptoideUtils.StringU.getResString(R.string.nothing_inserted_user);
          break;
      }
    }
    return message;
  }
}
