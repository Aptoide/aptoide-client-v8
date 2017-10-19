/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.view.recycler.widget;

import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.ApplicationPreferences;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.Translator;
import cm.aptoide.pt.view.recycler.displayable.FooterDisplayable;
import cm.aptoide.pt.view.store.StoreTabGridRecyclerFragment;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

public class FooterWidget extends Widget<FooterDisplayable> {

  private Button button;

  public FooterWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.button);
  }

  @Override public void bindView(FooterDisplayable displayable) {
    final AptoideApplication application =
        (AptoideApplication) getContext().getApplicationContext();
    final ApplicationPreferences appPreferences = application.getApplicationPreferences();
    final String marketName = appPreferences.getMarketName();
    final String buttonText =
        Translator.translate(displayable.getPojo().getActions().get(0).getLabel(),
            getContext().getApplicationContext(), marketName);
    button.setText(buttonText);

    final Action1<Void> handleButtonClick = __ -> {
      Event event = displayable.getPojo().getActions().get(0).getEvent();
      getFragmentNavigator().navigateTo(StoreTabGridRecyclerFragment.newInstance(event,
          Translator.translate(displayable.getPojo().getTitle(),
              getContext().getApplicationContext(), marketName), null, displayable.getTag(),
          displayable.getStoreContext(), false), true);
    };
    compositeSubscription.add(RxView.clicks(button).subscribe(handleButtonClick));
  }
}
