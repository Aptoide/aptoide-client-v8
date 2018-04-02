package cm.aptoide.pt.dataprovider.ws.v7.home;

import cm.aptoide.pt.dataprovider.model.v7.Review;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;

/**
 * Created by jdandrade on 26/03/2018.
 */

public class Card {
  private String type;
  private String uid;
  private App app;
  private Review.User user;

  public Card() {
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public App getApp() {
    return app;
  }

  public void setApp(App app) {
    this.app = app;
  }

  public Review.User getUser() {
    return user;
  }

  public void setUser(Review.User user) {
    this.user = user;
  }

  public boolean hasUser() {
    return user != null;
  }
}
