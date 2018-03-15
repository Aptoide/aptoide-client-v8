package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/14/18.
 */

public class UpdatesHeader extends Header {

  public UpdatesHeader(String title) {
    super(title);
  }

  @Override public Type getType() {
    return Type.HEADER_UPDATES;
  }
}
