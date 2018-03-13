package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/9/18.
 */

public class Header implements App {

  private String title;

  public Header(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  @Override public Type getType() {
    return Type.HEADER;
  }
}
