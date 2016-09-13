/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 06/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreGridRecyclerFragment;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.FooterDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 29/04/16.
 */
@Displayables({FooterDisplayable.class})
public class FooterWidget extends Widget<FooterDisplayable> {

	private Button button;

	public FooterWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		button = (Button) itemView.findViewById(R.id.button);
	}

	@Override
	public void bindView(FooterDisplayable displayable) {
		final GetStoreWidgets.WSWidget pojo = displayable.getPojo();
		final List<GetStoreWidgets.WSWidget.Action> actions = pojo.getActions();

		button.setText(Translator.translate(displayable.getPojo().getActions().get(0).getLabel()));
		button.setOnClickListener((view) -> {
			Event event = displayable.getPojo().getActions().get(0).getEvent();
			FragmentUtils.replaceFragmentV4((FragmentActivity) itemView.getContext(), StoreGridRecyclerFragment.newInstance(event, Translator.translate(displayable
					.getPojo()
					.getTitle())));
		});
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
