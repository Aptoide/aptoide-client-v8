package cm.aptoide.pt.account.view.store;

import android.content.res.Resources;
import android.support.annotation.StringRes;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.ErrorsMapper;

public class ManageStoreErrorMapper {

  private final Resources resources;
  private final ErrorsMapper errorsMapper;

  public ManageStoreErrorMapper(Resources resources, ErrorsMapper errorsMapper) {
    this.resources = resources;
    this.errorsMapper = errorsMapper;
  }

  public String getGenericError() {
    return resources.getString(R.string.all_message_general_error);
  }

  public String getImageError() {
    return resources.getString(R.string.ws_error_API_1);
  }

  public String getInvalidStoreError() {
    return resources.getString(R.string.ws_error_WOP_2);
  }

  public String getNetworkError(String errorCode, String applicationPackageName) {
    return resources.getString(
        errorsMapper.getWebServiceErrorMessageFromCode(errorCode, applicationPackageName,
            resources));
  }

  public String getError(SocialErrorType errorType) {
    return resources.getString(getErrorMessage(errorType));
  }

  @StringRes private int getErrorMessage(SocialErrorType errorType) {
    switch (errorType) {
      case INVALID_URL_TEXT:
        return R.string.edit_store_social_link_invalid_url_text;
      case LINK_CHANNEL_ERROR:
        return R.string.edit_store_social_link_channel_error;
      case PAGE_DOES_NOT_EXIST:
        return R.string.edit_store_page_doesnt_exist_error_short;
      default:
      case GENERIC_ERROR:
        return R.string.all_message_general_error;
    }
  }

  enum SocialErrorType {
    INVALID_URL_TEXT, LINK_CHANNEL_ERROR, PAGE_DOES_NOT_EXIST, GENERIC_ERROR
  }
}
