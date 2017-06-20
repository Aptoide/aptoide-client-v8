package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

public class Result {
  private ResultStatus status;
  private ResultError error;

  public ResultStatus getStatus() {
    return status;
  }

  public void setStatus(ResultStatus status) {
    this.status = status;
  }

  public ResultError getError() {
    return error;
  }

  public void setError(ResultError error) {
    this.error = error;
  }

  public enum ResultStatus {
    SUCC, FAIL
  }
}
