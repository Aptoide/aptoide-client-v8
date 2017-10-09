package cm.aptoide.pt.dataprovider.model.v7.store;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 21-09-2017.
 */

@Data @EqualsAndHashCode(callSuper = true)
public abstract class StoreUserAbstraction<T extends BaseV7Response> extends BaseV7Response {

  private Nodes<T> nodes;

  @Data public static class Nodes<T extends BaseV7Response> {
    private T meta;
    private GetStoreTabs tabs;
    private GetStoreWidgets widgets;
  }
}
