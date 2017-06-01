package cm.aptoide.pt.v8engine.social;

import cm.aptoide.pt.v8engine.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by jdandrade on 31/05/2017.
 */

interface TimelineView extends View {

  void showCards(List<Article> cards);

  void showProgressIndicator();

  void hideProgressIndicator();

  void hideRefresh();

  Observable<Void> refreshes();
}
