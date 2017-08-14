package cm.aptoide.pt.model.v7.base;

import java.util.List;

/**
 * Created by neuro on 20-04-2016.
 */
public class BaseV7Response {

  private Info info;
  private List<Error> errors;

  public Error getError() {
    if (errors != null && errors.size() > 0) {
      return errors.get(0);
    } else {
      return null;
    }
  }

  public Info getInfo() {
    return info;
  }

  public void setInfo(Info info) {
    this.info = info;
  }

  public List<Error> getErrors() {
    return errors;
  }

  public void setErrors(List<Error> errors) {
    this.errors = errors;
  }

  public boolean isOk() {
    return info != null && info.getStatus() == Info.Status.OK;
  }
}
