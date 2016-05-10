/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewDescriptionDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 10/05/16.
 */
@Displayables({AppViewDescriptionDisplayable.class})
public class AppViewDescriptionWidget extends Widget<AppViewDescriptionDisplayable> {

	private TextView description;

	private ImageView badgeMarket;
	private ImageView badgeSignature;
	private ImageView badgeFlag;
	private ImageView badgeAntivirus;

	private View see_more_layout;

	public AppViewDescriptionWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		description = (TextView) itemView.findViewById(R.id.description);

		badgeMarket = (ImageView) itemView.findViewById(R.id.iv_market_badge);
		badgeSignature = (ImageView) itemView.findViewById(R.id.iv_signature_badge);
		badgeFlag = (ImageView) itemView.findViewById(R.id.iv_flag_badge);
		badgeAntivirus = (ImageView) itemView.findViewById(R.id.iv_antivirus_badge);

		see_more_layout = itemView.findViewById(R.id.see_more_layout);
	}

	@Override
	public void bindView(AppViewDescriptionDisplayable displayable) {

	}
}
