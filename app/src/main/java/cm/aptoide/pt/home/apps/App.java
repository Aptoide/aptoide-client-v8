package cm.aptoide.pt.home.apps;

/**
 * Created by filipegoncalves on 3/8/18.
 */

public interface App {

  App.Type getType();

  String getIdentifier();

  enum Type {
    UPDATE, DOWNLOAD, INSTALLED, APPC_MIGRATION
  }
}
