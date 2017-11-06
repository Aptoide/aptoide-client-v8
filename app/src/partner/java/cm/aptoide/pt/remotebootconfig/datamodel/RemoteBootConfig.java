package cm.aptoide.pt.remotebootconfig.datamodel;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import lombok.Data;

/**
 * Created by diogoloureiro on 02/03/2017.
 *
 * Remote Boot Config class
 */

public class RemoteBootConfig {
  private BaseV7Response.Info info;
  private BootConfig data;

  /**
   * Remote Boot Config constructor
   *
   * @param info base request info
   * @param data base boot config data
   */
  public RemoteBootConfig(BaseV7Response.Info info, BootConfig data) {
    this.info = info;
    this.data = data;
  }

  public BaseV7Response.Info getInfo() {
    return info;
  }

  public BootConfig getData() {
    return data;
  }
}
