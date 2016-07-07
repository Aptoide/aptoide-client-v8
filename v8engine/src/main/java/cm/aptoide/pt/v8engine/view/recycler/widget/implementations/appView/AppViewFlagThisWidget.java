/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.TextView;

import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView.AppViewFlagThisDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by sithengineer on 30/06/16.
 */
@Displayables({AppViewFlagThisDisplayable.class})
public class AppViewFlagThisWidget extends Widget<AppViewFlagThisDisplayable> {

	private static final String TAG = AppViewFlagThisWidget.class.getSimpleName();

	private TextView workingWellText;
	private TextView needsLicenceText;
	private TextView fakeAppText;
	private TextView virusText;

	public AppViewFlagThisWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		workingWellText = (TextView) itemView.findViewById(R.id.working_well_count);
		needsLicenceText = (TextView) itemView.findViewById(R.id.needs_licence_count);
		fakeAppText = (TextView) itemView.findViewById(R.id.fake_app_count);
		virusText = (TextView) itemView.findViewById(R.id.virus_count);
	}

	@Override
	public void bindView(AppViewFlagThisDisplayable displayable) {
		GetApp pojo = displayable.getPojo();

		try {
			GetAppMeta.GetAppMetaFile.Flags flags = pojo.getNodes().getMeta().getData().getFile().getFlags();
			if (flags != null && flags.getVotes() != null && !flags.getVotes().isEmpty()) {
				for (final GetAppMeta.GetAppMetaFile.Flags.Vote vote : flags.getVotes()) {
					applyCount(vote.getType(), vote.getCount());
				}
			}
		} catch (NullPointerException ex) {
			Logger.e(TAG, ex);
		}

		// TODO set name labels according to localization needs
	}

	private void applyCount(GetAppMeta.GetAppMetaFile.Flags.Vote.Type type, int count) {
		String countAsString = Integer.toString(count);
		switch (type) {
			case GOOD:
				workingWellText.setText(countAsString);
				break;

			case VIRUS:
				virusText.setText(countAsString);
				break;

			case FAKE:
				fakeAppText.setText(countAsString);
				break;

			case LICENSE:
				needsLicenceText.setText(countAsString);
				break;

			case FREEZE:
				// un-used type
				break;

			default:
				throw new IllegalArgumentException("Unable to find Type " + type.name());
		}
	}
}
