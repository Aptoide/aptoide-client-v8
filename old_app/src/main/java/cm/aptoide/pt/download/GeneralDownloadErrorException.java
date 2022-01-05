package cm.aptoide.pt.download;

import cm.aptoide.pt.utils.BaseException;

class GeneralDownloadErrorException extends BaseException {
  public GeneralDownloadErrorException(String errorMessage) {
    super(errorMessage);
  }
}
