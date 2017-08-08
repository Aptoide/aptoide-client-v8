package cm.aptoide.pt.remotebootconfig.datamodel;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import lombok.Data;

/**
 * Created by diogoloureiro on 02/03/2017.
 */

@Data public class RemoteBootConfig {
  private BaseV7Response.Info info;
  private BootConfig data;
}
