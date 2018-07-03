package cm.aptoide.pt.abtesting;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;

/**
 * Created by franciscocalado on 19/06/18.
 */

public class ABTestImpressionResponse extends BaseBody {
  private boolean cache;
  private String payload;
  private String assignment;
  private String context;
  private String status;

  public boolean isCache() {
    return cache;
  }

  public void setCache(boolean cache) {
    this.cache = cache;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public String getAssignment() {
    return assignment;
  }

  public void setAssignment(String assignment) {
    this.assignment = assignment;
  }

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}

