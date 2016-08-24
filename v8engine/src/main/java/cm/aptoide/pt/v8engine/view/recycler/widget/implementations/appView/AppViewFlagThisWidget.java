/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 12/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.appView;

import android.view.View;
import android.widget.TextView;

import java.util.Map;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.v2.ErrorResponse;
import cm.aptoide.pt.dataprovider.ws.v3.AddApkFlagRequest;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.utils.ShowMessage;
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

	private final Map<Integer,GetAppMeta.GetAppMetaFile.Flags.Vote.Type> viewIdTypeMap;

	private View goodAppLayoutWrapper;
	private View flagsLayoutWrapper;

	private View workingWellLayout;
	private View needsLicenseLayout;
	private View fakeAppLayout;
	private View virusLayout;

	private TextView workingWellText;
	private TextView needsLicenceText;
	private TextView fakeAppText;
	private TextView virusText;


	public AppViewFlagThisWidget(View itemView) {
		super(itemView);
		viewIdTypeMap = new HashMapNotNull<>();
		viewIdTypeMap.put(R.id.working_well_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.GOOD);
		viewIdTypeMap.put(R.id.needs_licence_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.LICENSE);
		viewIdTypeMap.put(R.id.fake_app_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.FAKE);
		viewIdTypeMap.put(R.id.virus_layout, GetAppMeta.GetAppMetaFile.Flags.Vote.Type.VIRUS);
	}

	@Override
	protected void assignViews(View itemView) {
		goodAppLayoutWrapper = itemView.findViewById(R.id.good_app_layout);
		flagsLayoutWrapper = itemView.findViewById(R.id.rating_flags_layout);

		workingWellLayout = itemView.findViewById(R.id.working_well_layout);
		needsLicenseLayout = itemView.findViewById(R.id.needs_licence_layout);
		fakeAppLayout = itemView.findViewById(R.id.fake_app_layout);
		virusLayout = itemView.findViewById(R.id.virus_layout);

		workingWellText = (TextView) itemView.findViewById(R.id.working_well_count);
		needsLicenceText = (TextView) itemView.findViewById(R.id.needs_licence_count);
		fakeAppText = (TextView) itemView.findViewById(R.id.fake_app_count);
		virusText = (TextView) itemView.findViewById(R.id.virus_count);
	}

	@Override
	public void bindView(AppViewFlagThisDisplayable displayable) {
		GetApp pojo = displayable.getPojo();
		GetAppMeta.App app = pojo.getNodes().getMeta().getData();

		if (app.getFile().isGoodApp()) {
			goodAppLayoutWrapper.setVisibility(View.VISIBLE);
			flagsLayoutWrapper.setVisibility(View.GONE);
		} else {
			goodAppLayoutWrapper.setVisibility(View.GONE);
			flagsLayoutWrapper.setVisibility(View.VISIBLE);
			bindFlagViews(app);
		}
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}

	private void bindFlagViews(GetAppMeta.App app) {
		try {
			GetAppMeta.GetAppMetaFile.Flags flags = app.getFile().getFlags();
			if (flags != null && flags.getVotes() != null && !flags.getVotes().isEmpty()) {
				for (final GetAppMeta.GetAppMetaFile.Flags.Vote vote : flags.getVotes()) {
					applyCount(vote.getType(), vote.getCount());
				}
			}
		} catch (NullPointerException ex) {
			Logger.e(TAG, ex);
		}

		View.OnClickListener buttonListener = handleButtonClick(app.getStore().getName(), app.getFile().getMd5sum());
		workingWellLayout.setOnClickListener(buttonListener);
		needsLicenseLayout.setOnClickListener(buttonListener);
		fakeAppLayout.setOnClickListener(buttonListener);
		virusLayout.setOnClickListener(buttonListener);
	}

	private View.OnClickListener handleButtonClick(final String storeName, final String md5) {
		return v -> {

			if (!AptoideAccountManager.isLoggedIn()) {
				ShowMessage.asSnack(v, R.string.you_need_to_be_logged_in, R.string.login, snackView -> {
					AptoideAccountManager.openAccountManager(snackView.getContext());
				});
				return;
			}

			//ShowMessage.asSnack(v, R.string.casting_vote);
			setButtonPressed(v);

			final GetAppMeta.GetAppMetaFile.Flags.Vote.Type type = viewIdTypeMap.get(v.getId());

			AddApkFlagRequest.of(storeName, md5, type.name().toLowerCase()).execute(response -> {
				if (response.isOk() && !response.hasErrors()) {
					boolean voteSubmitted = false;
					switch (type) {
						case GOOD:
							voteSubmitted = true;
							workingWellText.setText(Integer.toString(Integer.parseInt(workingWellText.getText().toString()) + 1));
							break;

						case LICENSE:
							voteSubmitted = true;
							needsLicenceText.setText(Integer.toString(Integer.parseInt(needsLicenceText.getText().toString()) + 1));
							break;

						case FAKE:
							voteSubmitted = true;
							fakeAppText.setText(Integer.toString(Integer.parseInt(fakeAppText.getText().toString()) + 1));
							break;

						case VIRUS:
							voteSubmitted = true;
							virusText.setText(Integer.toString(Integer.parseInt(virusText.getText().toString()) + 1));
							break;

						case FREEZE:
							// un-used type
							break;

						default:
							throw new IllegalArgumentException("Unable to find Type " + type.name());
					}

					if (voteSubmitted) {
						ShowMessage.asSnack(v, R.string.vote_submitted);
						return;
					}
				}

				if (response.hasErrors()) {
					for (final ErrorResponse errorResponse : response.getErrors()) {
						Logger.e(TAG, errorResponse.getErrorDescription());
					}
				}

				setAllButtonsUnPressed(v);
				ShowMessage.asSnack(v, R.string.unknown_error);
			}, error -> {
				Logger.e(TAG, error);
				setAllButtonsUnPressed(v);
				ShowMessage.asSnack(v, R.string.unknown_error);
			}, true);
		};
	}

	private void setButtonPressed(View v) {
		v.setSelected(true);
		v.setPressed(false);
		workingWellLayout.setClickable(false);
		needsLicenseLayout.setClickable(false);
		fakeAppLayout.setClickable(false);
		virusLayout.setClickable(false);
	}

	private void setAllButtonsUnPressed(View v) {
		workingWellLayout.setClickable(true);
		needsLicenseLayout.setClickable(true);
		fakeAppLayout.setClickable(true);
		virusLayout.setClickable(true);
		v.setSelected(false);
		v.setPressed(false);
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
