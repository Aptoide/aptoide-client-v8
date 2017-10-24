package cm.aptoide.pt.view.app;

import java.util.Collections;
import java.util.List;

/**
 * Created by trinkes on 24/10/2017.
 */
public class AppsList {
  private final List<Application> list;
  private final boolean loading;
  private final Error error;

  public AppsList(Error error) {
    this.error = error;
    this.loading = false;
    list = Collections.emptyList();
  }

  public AppsList(List<Application> list, boolean loading) {
    this.list = list;
    this.loading = loading;
    error = null;
  }

  public AppsList(boolean loading) {
    this.loading = loading;
    list = Collections.emptyList();
    error = null;
  }

  public List<Application> getList() {
    return list;
  }

  public boolean isLoading() {
    return loading;
  }

  public Error getError() {
    return error;
  }

  public boolean hasErrors() {
    return error != null;
  }

  public enum Error {
    NETWORK, GENERIC,
  }
}
