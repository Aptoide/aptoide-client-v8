/*
 * Copyright (c) 2016.
 * Modified on 06/07/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import java.util.List;

public class BundlesDataList {

  private int total;
  private int count;
  private int offset;
  private Integer limit;
  private int next;
  private List<Bundle> list;

  public BundlesDataList() {
  }

  public int getTotal() {
    return this.total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getCount() {
    return this.count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public Integer getLimit() {
    return this.limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public int getNext() {
    return this.next;
  }

  public void setNext(int next) {
    this.next = next;
  }

  public List<Bundle> getList() {
    return this.list;
  }

  public void setList(List<Bundle> list) {
    this.list = list;
  }

  public static class Bundle {
    private String type;
    private String title;
    private String tag;
    private List<App> apps;

    public Bundle() {
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getTag() {
      return tag;
    }

    public void setTag(String tag) {
      this.tag = tag;
    }

    public List<App> getApps() {
      return apps;
    }

    public void setApps(List<App> apps) {
      this.apps = apps;
    }
  }
}
