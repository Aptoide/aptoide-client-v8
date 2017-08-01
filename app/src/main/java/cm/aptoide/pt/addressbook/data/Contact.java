package cm.aptoide.pt.addressbook.data;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import lombok.Data;

/**
 * Created by jdandrade on 13/02/2017.
 */
@Data public class Contact {
  private Comment.User person;
  private Store store;
}
