package cm.aptoide.pt.v8engine.timeline.response;

import java.util.List;

public final class Response<T> {
  private T data;
  private Info info;
  private List<RemoteError> errors;

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public Info getInfo() {
    return info;
  }

  public void setInfo(Info info) {
    this.info = info;
  }

  public List<RemoteError> getErrors() {
    return errors;
  }

  public void setErrors(List<RemoteError> errors) {
    this.errors = errors;
  }
}
