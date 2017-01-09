package cm.aptoide.pt.dataprovider.util;

import java.util.List;
import rx.Observable;

/**
 * Created by neuro on 03-01-2017.
 */

public interface EndlessController<T> {
  Observable<List<T>> get();

  Observable<List<T>> loadMore(boolean bypassCache);
}
