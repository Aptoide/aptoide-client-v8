/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/06/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by neuro on 07-06-2016.
 */
public class ProgressBarDisplayable extends Displayable {

	public ProgressBarDisplayable() {
	}

	public ProgressBarDisplayable(boolean fixedPerLineCount) {
		super(fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.PROGRESS_DISPLAYABLE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.row_progress_bar;
	}
}
