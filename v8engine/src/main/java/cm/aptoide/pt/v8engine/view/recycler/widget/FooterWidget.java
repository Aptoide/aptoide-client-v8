/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.Translator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.FooterDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.functions.Action1;

@Displayables({ FooterDisplayable.class }) public class FooterWidget
    extends Widget<FooterDisplayable> {

  private Button button;

  public FooterWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    button = (Button) itemView.findViewById(R.id.button);
  }

  @Override public void bindView(FooterDisplayable displayable) {
    final String buttonText = Translator.translate(displayable.getPojo()
        .getActions()
        .get(0)
        .getLabel());
    button.setText(buttonText);

    final Action1<Void> handleButtonClick = __ -> {
      Event event = displayable.getPojo()
          .getActions()
          .get(0)
          .getEvent();
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newStoreTabGridRecyclerFragment(event, Translator.translate(displayable.getPojo()
              .getTitle()), null, displayable.getTag(), displayable.getStoreContext()));
    };
    compositeSubscription.add(RxView.clicks(button)
        .subscribe(handleButtonClick));
  }
}
