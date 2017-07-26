package cm.aptoide.pt.v8engine.timeline.response;

import cm.aptoide.pt.dataprovider.model.v7.DataList;
import java.util.List;

public final class ResponseList<T extends DataList> {
  private T datalist;
  private List<RemoteError> errors;

  public T getDatalist() {
    return datalist;
  }

  public void setDatalist(T datalist) {
    this.datalist = datalist;
  }

  public List<RemoteError> getErrors() {
    return errors;
  }

  public void setErrors(List<RemoteError> errors) {
    this.errors = errors;
  }
}
