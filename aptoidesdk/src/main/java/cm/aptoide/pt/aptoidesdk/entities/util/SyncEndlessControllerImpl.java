package cm.aptoide.pt.aptoidesdk.entities.util;

import cm.aptoide.pt.dataprovider.interfaces.EndlessController;
import java.util.List;

/**
 * Created by neuro on 09-01-2017.
 */
public class SyncEndlessControllerImpl<U> implements SyncEndlessController<U> {

  private final EndlessController<U> endlessController;

  public SyncEndlessControllerImpl(EndlessController<U> endlessController) {
    this.endlessController = endlessController;
  }

  @Override public List<U> get() {
    return endlessController.get().toBlocking().first();
  }

  @Override public List<U> loadMore() {
    return endlessController.loadMore().toBlocking().first();
  }
}
