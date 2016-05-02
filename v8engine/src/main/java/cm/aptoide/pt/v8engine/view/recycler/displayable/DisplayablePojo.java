/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 14-04-2016.
 */
public abstract class DisplayablePojo<T> extends Displayable {

	//private static final float REFERENCE_WIDTH_DPI = 360;
	@Getter @Setter private T pojo;

	/**
	 * Needed for reflective {@link Class#newInstance()}.
	 */
	public DisplayablePojo() {
	}

	public DisplayablePojo(T pojo) {
		this.pojo = pojo;
	}

	public DisplayablePojo(T pojo, boolean fixedPerLineCount) {
		super(fixedPerLineCount);
		this.pojo = pojo;
	}
}
