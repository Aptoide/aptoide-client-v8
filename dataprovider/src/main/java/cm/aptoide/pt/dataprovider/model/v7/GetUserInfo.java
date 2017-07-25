package cm.aptoide.pt.dataprovider.model.v7;

import lombok.Data;

/**
 * Created by pedroribeiro on 01/06/17.
 *
 * This pojo is returned by a new request to user/get with two of it's nodes (meta and settings)
 * It is called GetUserInfo because this is replacing the old getUserInfo request and pojo and a
 * GetUserRequest already existed.
 */

@Data public class GetUserInfo extends BaseV7Response {

  private Nodes nodes;

  @Data public static class Nodes {
    private GetUserMeta meta;
    private GetUserSettings settings;
  }
}
