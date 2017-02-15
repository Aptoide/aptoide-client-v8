package cm.aptoide.pt.dataprovider.interfaces;

import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 03-01-2017.
 */
public interface EndlessController<U> {

  /**
   * @return All the results retrieved so far.
   */
  Observable<List<U>> get();

  /**
   * Load more objects.
   *
   * @return a new set of objects.
   */
  Observable<List<U>> loadMore();
}
