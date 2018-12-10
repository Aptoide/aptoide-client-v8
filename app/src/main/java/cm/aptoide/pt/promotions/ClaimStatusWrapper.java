package cm.aptoide.pt.promotions;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import java.util.ArrayList;
import java.util.List;

public class ClaimStatusWrapper {

  private static final String WRONG_CAPTCHA = "PROMOTION-1";
  private static final String WRONG_ADDRESS = "PROMOTION-2";
  private static final String ALREADY_CLAIMED = "PROMOTION-3";

  private final Status status;
  private final List<Error> errors;

  public ClaimStatusWrapper(Status status, List<BaseV7Response.Error> errors) {

    this.status = status;
    this.errors = map(errors);
  }

  public Status getStatus() {
    return status;
  }

  public List<Error> getErrors() {
    return errors;
  }

  private List<Error> map(List<BaseV7Response.Error> errors) {
    List<Error> result = new ArrayList<>();
    if (errors != null) {
      for (BaseV7Response.Error error : errors) {
        if (error.getCode()
            .equals(WRONG_CAPTCHA)) {
          result.add(Error.wrongCaptcha);
        } else if (error.getCode()
            .equals(WRONG_ADDRESS)) {
          result.add(Error.wrongAddress);
        } else if (error.getCode()
            .equals(ALREADY_CLAIMED)) {
          result.add(Error.promotionClaimed);
        } else {
          result.add(Error.generic);
        }
      }
    }
    return result;
  }

  public enum Status {
    ok, fail
  }

  public enum Error {
    promotionClaimed, wrongAddress, wrongCaptcha, generic
  }
}
