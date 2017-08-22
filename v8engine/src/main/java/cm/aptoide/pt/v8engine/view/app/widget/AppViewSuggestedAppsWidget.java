package cm.aptoide.pt.v8engine.view.app.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewAdDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewSuggestedAppDisplayable;
import cm.aptoide.pt.v8engine.view.app.displayable.AppViewSuggestedAppsDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class AppViewSuggestedAppsWidget extends Widget<AppViewSuggestedAppsDisplayable> {

  private RecyclerView recyclerView;
  private TextView similarAppsToTextView;

  public AppViewSuggestedAppsWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    recyclerView = (RecyclerView) itemView.findViewById(R.id.appview_suggested_recycler_view);
    similarAppsToTextView = (TextView) itemView.findViewById(R.id.similar_apps_to);
  }

  @Override public void bindView(AppViewSuggestedAppsDisplayable displayable) {
    final List<MinimalAd> minimalAds = displayable.getMinimalAds();
    final List<App> appsList = displayable.getAppsList();

    List<Displayable> displayables = new LinkedList<>();
    for (MinimalAd minimalAd : minimalAds) {
      // TODO: 01-08-2017 neuro fill ad tag
      displayables.add(new AppViewAdDisplayable(minimalAd));
    }

    for (App app : appsList) {
      // TODO: 01-08-2017 neuro fill app tag
      app.getStore()
          .setAppearance(new Store.Appearance());
      displayables.add(
          new AppViewSuggestedAppDisplayable(app, displayable.getAppViewSimilarAppAnalytics()));
    }

    BaseAdapter adapter = new BaseAdapter(displayables) {
      @Override public int getItemCount() {
        return super.getItemCount();
      }
    };

    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), GridLayoutManager.HORIZONTAL, false));
    recyclerView.setNestedScrollingEnabled(false);
    recyclerView.setAdapter(adapter);

    similarAppsToTextView.setText(String.format(Locale.getDefault(), getContext().getResources()
        .getString(R.string.appview_title_you_might_also_like)));
  }
}
