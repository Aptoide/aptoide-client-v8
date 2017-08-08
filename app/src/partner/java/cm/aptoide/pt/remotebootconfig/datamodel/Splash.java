package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 */

@Data @AllArgsConstructor public class Splash {
  private boolean enable;
  private String portrait;
  private String landscape;
  private int timeout;
}