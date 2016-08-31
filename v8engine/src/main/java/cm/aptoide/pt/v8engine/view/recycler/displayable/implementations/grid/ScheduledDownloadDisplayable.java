/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SelectableDisplayablePojo;
import lombok.Getter;
import lombok.Setter;

/**
 * created by SithEngineer
 */
public class ScheduledDownloadDisplayable extends SelectableDisplayablePojo<Scheduled> {

	@Getter @Setter
	private ProgressBar progressBarIsInstalling;

	@Getter @Setter
	private CheckBox isSelected;

	public ScheduledDownloadDisplayable() { }

	public ScheduledDownloadDisplayable(Scheduled pojo) {
		super(pojo);
	}

	public ScheduledDownloadDisplayable(Scheduled pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.SCHEDULED_DOWNLOAD;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_scheduled_download_row;
	}

	public void updateUi(boolean isInstalling) {
		progressBarIsInstalling.setVisibility(isInstalling ? View.VISIBLE : View.GONE);
		isSelected.setVisibility(isInstalling ? View.GONE : View.VISIBLE);
	}
}
