package cm.aptoide.pt.download;

import cm.aptoide.pt.utils.BaseException;

public class InvalidAppException extends BaseException {

  public InvalidAppException(String detailMessage) {
    super(detailMessage);
  }
}
