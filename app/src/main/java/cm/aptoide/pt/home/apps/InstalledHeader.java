package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/20/18.
 */

public class InstalledHeader extends Header {

  public InstalledHeader(String title) {
    super(title);
  }

  @Override public Type getType() {
    return Type.HEADER_INSTALLED;
  }
}
