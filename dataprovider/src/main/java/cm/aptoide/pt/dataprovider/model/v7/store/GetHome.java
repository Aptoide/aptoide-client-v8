package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by trinkes on 23/02/2017.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetHome extends BaseV7Response {

  private Nodes nodes;

  @Data public static class Nodes {

    private GetHomeMeta meta;
    private GetStoreTabs tabs;
    private GetStoreWidgets widgets;
  }
}
