package cm.aptoide.pt.addressbook.data;

import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;

/**
 * Created by jdandrade on 13/02/2017.
 */
public class Contact {
  private Comment.User person;
  private Store store;

  public Comment.User getPerson() {
    return person;
  }

  public void setPerson(Comment.User person) {
    this.person = person;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }
}
