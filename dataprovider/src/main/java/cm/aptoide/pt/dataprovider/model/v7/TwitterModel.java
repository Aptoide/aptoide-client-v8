package cm.aptoide.pt.model.v7;

import lombok.Data;

/**
 * Created by pedroribeiro on 23/02/17.
 *
 * Twitter user information for followers extraction (this information is sent to userconnection)
 */

@Data public class TwitterModel {

  private long id;
  private String token;
  private String secret;
}
