package cm.aptoide.pt.account.view;

import android.content.Context;
import android.content.res.Resources;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.exception.InvalidImageException;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.utils.GenericDialogs;
import rx.Observable;

public class ImagePickerErrorHandler {

  private final Context context;
  private final Resources resources;
  private final ThemeManager themeManager;

  public ImagePickerErrorHandler(Context context, ThemeManager themeManager) {
    this.context = context;
    this.resources = context.getResources();
    this.themeManager = themeManager;
  }

  public Observable<GenericDialogs.EResponse> showIconPropertiesError(
      InvalidImageException exception) {
    String errors = buildErrorMessage(exception);
    return GenericDialogs.createGenericOkMessage(context,
        resources.getString(R.string.image_requirements_error_popup_title), errors,
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId);
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
