/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 18/04/2016.
 */

package cm.aptoide.pt.v8engine.implementationsToRemove;

/**
 * Created by neuro on 16-04-2016.
 */
public class DisplayableImp2 extends DisplayableImp {

	@Override
	public String getName() {
		return "IMPL_2";
	}

	@Override
	public int getDefaultPerLineCount() {
		return super.getDefaultPerLineCount() * 3;
	}
}
