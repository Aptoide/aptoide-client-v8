package cm.aptoide.pt.v8engine.addressbook.data;

import cm.aptoide.pt.model.v7.Comment;
import cm.aptoide.pt.model.v7.store.Store;
import lombok.Data;

/**
 * Created by jdandrade on 13/02/2017.
 */
@Data public class Contact {
  private Comment.User person;
  private Store store;
}
