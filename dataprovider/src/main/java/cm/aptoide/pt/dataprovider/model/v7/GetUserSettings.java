package cm.aptoide.pt.dataprovider.model.v7;

import lombok.Data;

/**
 * Created by pedroribeiro on 01/06/17.
 */

@Data public class GetUserSettings extends BaseV7Response {

  private Data data;

  @lombok.Data public static class Data {
    private boolean mature;
    private Access access;
  }

  @lombok.Data public static class Access {
    private boolean confirmed;
  }
}
