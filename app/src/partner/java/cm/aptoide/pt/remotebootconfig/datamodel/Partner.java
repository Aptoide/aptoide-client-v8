package cm.aptoide.pt.remotebootconfig.datamodel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by diogoloureiro on 18/01/2017.
 */

@Data @AllArgsConstructor public class Partner {
  private int config_id;
  private String uid;
  private String type;
  private Feedback feedback;
  private Store store;
  private Appearance appearance;
  private Switches switches;
  private Social social;
}