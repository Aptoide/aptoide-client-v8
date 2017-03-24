package cm.aptoide.pt.v8engine.view;

import android.content.Context;
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
      message = context.getString(R.string.user_upload_photo_failed);
    }
    return message;
  }
}
