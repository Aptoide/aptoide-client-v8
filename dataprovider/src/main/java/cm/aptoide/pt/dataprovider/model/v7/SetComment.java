package cm.aptoide.pt.dataprovider.model.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by jdandrade on 08/02/2017.
 */

@Data @EqualsAndHashCode(callSuper = true) public class SetComment extends BaseV7Response {

  private Data data;

  @lombok.Data public static class Data {
    private long id;
    private String body;
    private Comment.User user;
    private String status;
    private String mode;
  }
}
