/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView
		.AppViewDeveloperDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 04/05/16.
 */
@Displayables({AppViewDeveloperDisplayable.class})
public class AppViewDeveloperWidget extends Widget<AppViewDeveloperDisplayable> {

	private TextView websiteLabel;
	private TextView emailLabel;
	private TextView privacyPolicyLabel;
	private TextView permissionsLabel;

	public AppViewDeveloperWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		websiteLabel = (TextView) itemView.findViewById(R.id.website_label);
		emailLabel = (TextView) itemView.findViewById(R.id.email_label);
		privacyPolicyLabel = (TextView) itemView.findViewById(R.id.privacy_policy_label);
		permissionsLabel = (TextView) itemView.findViewById(R.id.permissions_label);
	}

	@Override
	public void bindView(AppViewDeveloperDisplayable displayable) {
		final GetAppMeta.App app = displayable.getPojo();

		if(!TextUtils.isEmpty(app.getDeveloper().getWebsite())) {
			websiteLabel.setText(app.getDeveloper().getWebsite());
		} else {
			websiteLabel.setVisibility(View.GONE);
		}

		if(!TextUtils.isEmpty(app.getDeveloper().getEmail())) {
			emailLabel.setText(app.getDeveloper().getEmail());
		} else {
			emailLabel.setVisibility(View.GONE);
		}

		if(!TextUtils.isEmpty(app.getDeveloper().getPrivacy())) {
			privacyPolicyLabel.setText(app.getDeveloper().getPrivacy());
		} else {
			privacyPolicyLabel.setVisibility(View.GONE);
		}

		permissionsLabel.setOnClickListener(
				v -> Toast.makeText(v.getContext(), "TO DO", Toast.LENGTH_SHORT)
						.show()
		);
	}
}
