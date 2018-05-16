package cm.aptoide.pt.app.view;

import cm.aptoide.pt.app.AppViewSimilarApp;

/**
 * Created by franciscocalado on 14/05/18.
 */

public class SimilarAppClickEvent {

  private AppViewSimilarApp similar;
  private int position;

  public SimilarAppClickEvent(AppViewSimilarApp similar, int position) {
    this.similar = similar;
    this.position = position;
  }

  public AppViewSimilarApp getSimilar() {
    return similar;
  }

  public int getPosition() {
    return position;
  }
}
