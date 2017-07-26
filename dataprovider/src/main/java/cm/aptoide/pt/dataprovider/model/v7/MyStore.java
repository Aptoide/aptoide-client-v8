package cm.aptoide.pt.dataprovider.model.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by trinkes on 11/30/16.
 */

@Data @EqualsAndHashCode(callSuper = true) public class MyStore extends BaseV7Response {

  GetStoreWidgets widgets;
}
