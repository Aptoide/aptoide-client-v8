/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

/**
 * Created by sithengineer on 27/07/16.
 */
public abstract class SelectableDisplayablePojo<T> extends DisplayablePojo<T> {

	private boolean selected;

	public SelectableDisplayablePojo() {
	}

	public SelectableDisplayablePojo(T pojo) {
		super(pojo);
	}

	public SelectableDisplayablePojo(T pojo, boolean fixedPerLineCount) {
		super(pojo, fixedPerLineCount);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
