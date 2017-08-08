package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 */

@Data @AllArgsConstructor public class FirstInstall {
  private boolean enable;
  private boolean offline;
}