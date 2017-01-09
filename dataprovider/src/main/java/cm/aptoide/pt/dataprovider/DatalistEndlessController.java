package cm.aptoide.pt.dataprovider;

import cm.aptoide.pt.dataprovider.interfaces.EndlessController;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7EndlessDatalistResponse;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 03-01-2017.
 */
public class DatalistEndlessController<U> implements EndlessController<U> {

  private final V7<? extends BaseV7EndlessDatalistResponse<U>, ? extends Endless> v7request;
  private int total;
  private int offset;
  private boolean loading;
  private boolean stableData;
  private List<U> list = new LinkedList<>();

  public DatalistEndlessController(
      V7<? extends BaseV7EndlessDatalistResponse<U>, ? extends Endless> v7request) {
    this.v7request = v7request;
  }

  @Override public Observable<List<U>> get() {
    return Observable.just(list);
  }

  public Observable<List<U>> loadMore(boolean bypassCache) {

    if (!loading) {
      if (hasMoreElements()) {
        return v7request.observe(bypassCache)
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(() -> {
              loading = true;
            })
            .doOnError(error -> {
              //remove spinner if webservice respond with error
              loading = false;
            })
            .map(response -> {

              List<U> list;

              if (response.hasData()) {

                stableData = response.hasStableTotal();
                if (stableData) {
                  total = response.getTotal();
                  offset = response.getNextSize();
                } else {
                  total += response.getTotal();
                  offset += response.getNextSize();
                }
                v7request.getBody().setOffset(offset);
                list = response.getDatalist().getList();
              } else {
                list = Collections.emptyList();
              }

              loading = false;

              this.list.addAll(list);
              return list;
            });
      } else {
        return Observable.just(Collections.emptyList());
      }
    } else {
      return Observable.empty();
    }
  }

  private boolean hasMoreElements() {
    return (stableData) ? offset < total : offset <= total;
  }
}
