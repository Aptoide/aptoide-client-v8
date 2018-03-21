package cm.aptoide.pt.home;

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

  public HomeBundlesModel(Error error) {
    this.error = error;
    this.loading = false;
    list = Collections.emptyList();
    offset = -1;
  }

  public HomeBundlesModel(List<HomeBundle> list, boolean loading, int offset) {
    this.list = list;
    this.loading = loading;
    this.offset = offset;
    error = null;
  }

  public HomeBundlesModel(boolean loading) {
    this.loading = loading;
    list = Collections.emptyList();
    error = null;
    offset = -1;
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

  public enum Error {
    NETWORK, GENERIC,
  }
}
