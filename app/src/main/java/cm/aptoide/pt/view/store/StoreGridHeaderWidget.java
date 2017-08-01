package cm.aptoide.pt.view.store;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.recycler.widget.Displayables;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

@Displayables({ StoreGridHeaderDisplayable.class }) public class StoreGridHeaderWidget
    extends Widget<StoreGridHeaderDisplayable> {

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

    title.setText(Translator.translate(wsWidget.getTitle(), getContext().getApplicationContext()));

    more.setVisibility(moreIsVisible && displayable.isMoreVisible() ? View.VISIBLE : View.GONE);

    if (moreIsVisible) {
      compositeSubscription.add(RxView.clicks(more)
          .subscribe(a -> {
            getFragmentNavigator().navigateUsing(wsWidget.getActions()
                    .get(0)
                    .getEvent(), displayable.getStoreTheme(), wsWidget.getTitle(), displayable.getTag(),
                displayable.getStoreContext());
            Analytics.AppViewViewedFrom.addStepToList(wsWidget.getTag());
          }));
    }
  }
}
