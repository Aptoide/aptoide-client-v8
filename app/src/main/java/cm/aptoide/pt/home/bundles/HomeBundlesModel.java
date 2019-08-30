package cm.aptoide.pt.home.bundles;

import cm.aptoide.pt.home.bundles.base.HomeBundle;
import java.util.Collections;
import java.util.List;

/**
 * Created by jdandrade on 16/03/2018.
 */

public class HomeBundlesModel {
  private final List<HomeBundle> list;
  private final boolean loading;
  private final Error error;
  private final int offset;
  private final boolean complete;

  public HomeBundlesModel(Error error) {
    this.error = error;
    this.loading = false;
    list = Collections.emptyList();
    offset = -1;
    complete = true;
  }

  public HomeBundlesModel(List<HomeBundle> list, boolean loading, int offset, boolean complete) {
    this.list = list;
    this.loading = loading;
    this.offset = offset;
    error = null;
    this.complete = complete;
  }

  public HomeBundlesModel(boolean loading) {
    this.loading = loading;
    list = Collections.emptyList();
    error = null;
    offset = -1;
    complete = false;
  }

  public int getOffset() {
    return offset;
  }

  public List<HomeBundle> getList() {
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

  public boolean isListEmpty() {
    return list.isEmpty();
  }

  public enum Error {
    NETWORK, GENERIC,
  }
}
