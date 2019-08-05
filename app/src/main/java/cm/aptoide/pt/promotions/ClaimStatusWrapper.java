package cm.aptoide.pt.promotions;

import java.util.List;

public class ClaimStatusWrapper {

  private final Status status;
  private final List<Error> errors;

  public ClaimStatusWrapper(Status status, List<Error> errors) {

    this.status = status;
    this.errors = errors;
  }

  public Status getStatus() {
    return status;
  }

  public List<Error> getErrors() {
    return errors;
  }

  public enum Status {
    OK, FAIL
  }

  public enum Error {
    PROMOTION_CLAIMED, WRONG_ADDRESS, GENERIC, WALLET_NOT_VERIFIED
  }
}
