package cm.aptoide.pt.dataprovider.interfaces;

import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 03-01-2017.
 */
public interface EndlessControllerWithCache<U> extends EndlessController<U> {
  /**
   * Load more objects.
   *
   * @param bypassCache bypassCache.
   *
   * @return a new set of objects.
   */
  Observable<List<U>> loadMore(boolean bypassCache);
}
