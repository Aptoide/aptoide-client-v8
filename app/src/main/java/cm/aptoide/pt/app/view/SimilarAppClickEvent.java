package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.AppViewSimilarApp;

/**
 * Created by franciscocalado on 14/05/18.
 */

public class SimilarAppClickEvent {

  private AppViewSimilarApp similar;
  private String type;
  private int position;

  public SimilarAppClickEvent(AppViewSimilarApp similar, String type, int position) {
    this.similar = similar;
    this.type = type;
    this.position = position;
  }

  public AppViewSimilarApp getSimilar() {
    return similar;
  }

  public String getType() {
    return type;
  }

  public int getPosition() {
    return position;
  }
}
