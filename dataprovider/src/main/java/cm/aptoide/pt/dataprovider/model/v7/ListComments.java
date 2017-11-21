/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.dataprovider.model.v7;

/**
 * Created on 20/07/16.
 */
public class ListComments extends BaseV7EndlessDataListResponse<Comment> {

  public ListComments() {
  }

  public String toString() {
    return "ListComments()";
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ListComments)) return false;
    final ListComments other = (ListComments) o;
    if (!other.canEqual((Object) this)) return false;
    if (!super.equals(o)) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + super.hashCode();
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ListComments;
  }
}
