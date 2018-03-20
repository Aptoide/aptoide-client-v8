package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/20/18.
 */

public class DownloadsHeader extends Header {

  public DownloadsHeader(String title) {
    super(title);
  }

  @Override public Type getType() {
    return Type.HEADER_DOWNLOADS;
  }

}
