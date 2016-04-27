/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 18/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayable;

/**
 * Created by neuro on 14-04-2016.
 */
public class EmptyDisplayable extends Displayable {

	private int spanSize = 1;

	public EmptyDisplayable() {
	}

	public EmptyDisplayable(int spanSize) {
		this.spanSize = spanSize;
	}

	@Override
	public String getName() {
		return "EMPTY";
	}

	@Override
	public int getViewType() {
		return R.layout.empty_frame_layout;
	}

	@Override
	public int getDefaultPerLineCount() {
		// Stub
		return 1;
	}

	@Override
	public int getSpanSize() {
		return spanSize;
	}
}
