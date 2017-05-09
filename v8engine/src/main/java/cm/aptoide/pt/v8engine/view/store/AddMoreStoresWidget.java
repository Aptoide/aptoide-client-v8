/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.v8engine.view.store;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by neuro on 11-05-2016.
 */
@Displayables({ AddMoreStoresDisplayable.class }) public class AddMoreStoresWidget
    extends Widget<AddMoreStoresDisplayable> {

  private Button addMoreStores;

  public AddMoreStoresWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    addMoreStores = (Button) itemView.findViewById(R.id.add_more_stores);
  }

  @Override public void bindView(AddMoreStoresDisplayable displayable) {
    addMoreStores.setOnClickListener(v -> {
      if (itemView.getContext() instanceof FragmentActivity) {
        new AddStoreDialog().attachFragmentManager(getFragmentNavigator())
            .show(((FragmentActivity) itemView.getContext()).getSupportFragmentManager(),
                "addStoreDialog");
      }
    });
  }
}
