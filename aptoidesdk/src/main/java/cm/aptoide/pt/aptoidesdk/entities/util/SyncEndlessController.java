package cm.aptoide.pt.aptoidesdk.entities.util;

import java.util.List;

/**
 * Created by neuro on 09-01-2017.
 */
public interface SyncEndlessController<U> {

  /**
   * @return All the results retrieved so far.
   */
  List<U> get();

  /**
   * Load more objects.
   *
   * @return a new set of objects.
   */
  List<U> loadMore();
}
