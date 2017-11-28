package cm.aptoide.pt.remotebootconfig.datamodel;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

/**
 * Created by diogoloureiro on 02/03/2017.
 *
 * Remote Boot Config class
 */

public class RemoteBootConfig {
  private BaseV7Response.Info info;
  private BootConfig data;

  public BaseV7Response.Info getInfo() {
    return info;
  }

  public void setInfo(BaseV7Response.Info info) {
    this.info = info;
  }

  public BootConfig getData() {
    return data;
  }

  public void setData(BootConfig data) {
    this.data = data;
  }
}
