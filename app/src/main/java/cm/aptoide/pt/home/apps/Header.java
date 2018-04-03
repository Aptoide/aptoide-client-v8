package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/9/18.
 */

public class Header implements App {

  private Type type;

  public Header(Type type) {
    this.type = type;
  }

  @Override public Type getType() {
    return type;
  }
}
