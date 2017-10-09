package cm.aptoide.pt.view.store;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.util.CommentType;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

public class StoreGridHeaderWidget extends Widget<StoreGridHeaderDisplayable> {

  private TextView title;
  private Button more;

  public StoreGridHeaderWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    title = (TextView) itemView.findViewById(R.id.title);
    more = (Button) itemView.findViewById(R.id.more);
  }

  @Override public void bindView(StoreGridHeaderDisplayable displayable) {
    final GetStoreWidgets.WSWidget wsWidget = displayable.getWsWidget();
    final boolean moreIsVisible = wsWidget.hasActions();
    final String marketName =
        ((AptoideApplication) getContext().getApplicationContext()).getMarketName();
    final SharedPreferences sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    title.setText(Translator.translate(wsWidget.getTitle(), getContext().getApplicationContext(),
        marketName));

    more.setVisibility(moreIsVisible && displayable.isMoreVisible() ? View.VISIBLE : View.GONE);

    if (moreIsVisible) {
      compositeSubscription.add(RxView.clicks(more)
          .subscribe(a -> {

            final Event event = wsWidget.getActions()
                .get(0)
                .getEvent();
            final String storeTheme = displayable.getStoreTheme();
            final String tag = displayable.getTag();
            final StoreContext storeContext = displayable.getStoreContext();
            final String title = wsWidget.getTitle();

            if (event.getName() == Event.Name.listComments) {
              String action = event.getAction();
              String url =
                  action != null ? action.replace(V7.getHost(sharedPreferences), "") : null;
              displayable.getStoreTabNavigator()
                  .navigateToCommentGridRecyclerView(CommentType.STORE, url, "View Comments");
            } else {
              displayable.getStoreTabNavigator()
                  .navigateToStoreTabGridRecyclerView(event, title, storeTheme, tag, storeContext,
                      false);
            }

            Analytics.AppViewViewedFrom.addStepToList(wsWidget.getTag());
          }));
    }
  }
}
