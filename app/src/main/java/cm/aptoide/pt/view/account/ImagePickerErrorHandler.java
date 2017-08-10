package cm.aptoide.pt.view.account;

import android.content.Context;
import android.content.res.Resources;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.R;
import cm.aptoide.pt.view.account.exception.InvalidImageException;
import rx.Observable;

public class ImagePickerErrorHandler {

  private final Context context;
  private final Resources resources;

  public ImagePickerErrorHandler(Context context) {
    this.context = context;
    this.resources = context.getResources();
  }

  public Observable<GenericDialogs.EResponse> showIconPropertiesError(
      InvalidImageException exception) {
    String errors = buildErrorMessage(exception);
    return GenericDialogs.createGenericOkMessage(context,
        resources.getString(R.string.image_requirements_error_popup_title), errors);
  }

  private String buildErrorMessage(InvalidImageException err) {
    StringBuilder message = new StringBuilder();
    message.append(resources.getString(R.string.image_requirements_popup_message));
    for (InvalidImageException.ImageError imageSizeError : err.getImageErrors()) {
      switch (imageSizeError) {
        case MIN_HEIGHT:
          message.append(resources.getString(R.string.image_requirements_error_min_height));
          break;
        case MAX_HEIGHT:
          message.append(resources.getString(R.string.image_requirements_error_max_height));
          break;
        case MIN_WIDTH:
          message.append(resources.getString(R.string.image_requirements_error_min_width));
          break;
        case MAX_WIDTH:
          message.append(resources.getString(R.string.image_requirements_error_max_width));
          break;
        case MAX_IMAGE_SIZE:
          message.append(resources.getString(R.string.image_requirements_error_max_file_size));
          break;
        case ERROR_DECODING:
          message.append(resources.getString(R.string.image_requirements_error_open_image));
          break;
      }
    }

    int index = message.lastIndexOf("\n");
    if (index > 0) {
      message.delete(index, message.length());
    }

    return message.toString();
  }
}
