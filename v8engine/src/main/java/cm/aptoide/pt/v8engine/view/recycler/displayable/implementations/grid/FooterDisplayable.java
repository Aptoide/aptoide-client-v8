/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 10/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.DisplayablePojo;

/**
 * Created by sithengineer on 29/04/16.
 */
public class FooterDisplayable extends DisplayablePojo<GetStoreWidgets.WSWidget> {

	public FooterDisplayable() {
	}

	public FooterDisplayable(GetStoreWidgets.WSWidget pojo) {
		super(pojo);
	}

	@Override
	public Type getType() {
		return Type.FOOTER_ROW;
	}

	@Override
	public int getViewLayout() {
		return R.layout.button;
	}
}
