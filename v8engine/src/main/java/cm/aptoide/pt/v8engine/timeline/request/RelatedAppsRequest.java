package cm.aptoide.pt.v8engine.timeline.request;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;

public class RelatedAppsRequest extends BaseBody {

  private String url;

  public RelatedAppsRequest(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
