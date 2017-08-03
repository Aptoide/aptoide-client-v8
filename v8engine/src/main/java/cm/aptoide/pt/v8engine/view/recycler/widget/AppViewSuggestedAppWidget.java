package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.support.annotation.NonNull;
import android.view.View;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.v8engine.view.app.GridAppWidget;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewSuggestedAppDisplayable;
import rx.functions.Action1;

/**
 * Created by neuro on 01-08-2017.
 */

public class AppViewSuggestedAppWidget extends GridAppWidget<AppViewSuggestedAppDisplayable> {

  public AppViewSuggestedAppWidget(View itemView) {
    super(itemView);
  }

  @NonNull @Override
  protected Action1<Void> newOnClickListener(AppViewSuggestedAppDisplayable displayable, App pojo,
      long appId) {
    Action1<Void> superAction = super.newOnClickListener(displayable, pojo, appId);

    return aVoid -> {
      superAction.call(aVoid);
      displayable.getAppViewSimilarAppAnalytics()
          .openSimilarApp();
    };
  }
}
