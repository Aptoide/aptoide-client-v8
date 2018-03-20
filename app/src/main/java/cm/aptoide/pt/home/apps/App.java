package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public interface App {

  App.Type getType();

  enum Type {
    UPDATE, DOWNLOAD, INSTALLING, INSTALLED, HEADER_INSTALLED, HEADER_DOWNLOADS, HEADER_UPDATES

  }
}
