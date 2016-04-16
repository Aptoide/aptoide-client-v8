/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 16/04/2016.
 */

package cm.aptoide.pt.v8engine.implementationsToRemove;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.widget.DisplayablePojo;
import cm.aptoide.pt.v8engine.view.recycler.widget.WidgetEnum;

/**
 * Created by neuro on 14-04-2016.
 */
public class DisplayableImp extends DisplayablePojo<GetAdsResponse> {

	public DisplayableImp() {
	}

	public DisplayableImp(GetAdsResponse pojo) {
		super(pojo);
	}

	@Override
	public int getViewType() {
		return R.layout.widget_apagar;
	}

	@Override
	public int getDefaultPerLineCount() {
		return 3;
	}

	@Override
	public WidgetEnum getEnum() {
		return WidgetEnum.WIDGET_IMPL;
	}
}
