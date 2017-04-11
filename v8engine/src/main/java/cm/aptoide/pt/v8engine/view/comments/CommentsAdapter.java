package cm.aptoide.pt.v8engine.view.comments;

import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsAdapter<T extends Displayable> extends BaseAdapter {

  private final Class<T> clazz;

  public CommentsAdapter() {
    clazz = null;
  }

  public CommentsAdapter(Class<T> clazz) {
    this.clazz = clazz;
  }

  /**
   * Get the review position using the number of the review. For example, if
   * <code>reviewNumber</code> == 2, it will return the third review it finds in
   * <code>displayable</code>.
   *
   * @param itemNumber number of the review
   *
   * @return next review's position or -1 if there are no more reviews
   */
  public int getItemPosition(int itemNumber) {
    if (clazz == null) {
      return -1;
    }

    int toReturn = -1;

    int itemsCounter = 0;
    for (int i = 0; i < getItemCount(); i++) {
      if (clazz.isAssignableFrom(getDisplayable(i).getClass())) {
        if (itemsCounter == itemNumber) {
          toReturn = i;
          break;
        } else {
          itemsCounter++;
        }
      }
    }
    return toReturn;
  }
}
