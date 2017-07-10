package cm.aptoide.pt.v8engine.timeline.request;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;

public class CardPreviewRequest extends BaseBody {

  private final String url;

  public CardPreviewRequest(String url) {
    this.url = url;
  }

  public String getUrl() {
    return url;
  }
}
