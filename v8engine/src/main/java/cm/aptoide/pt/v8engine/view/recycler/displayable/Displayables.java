/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import java.util.Collection;
import java.util.LinkedList;

import cm.aptoide.pt.model.v7.Type;

/**
 * Created by sithengineer on 16/05/16.
 */
public class Displayables extends LinkedList<Displayable> {

	@Override
	public boolean addAll(Collection<? extends Displayable> collection) {
		for (Displayable displayable : collection) {
			if(displayable.getType() == Type.GROUP) {
				addAll( ((DisplayableGroup) displayable).getChildren() );
			} else {
				add(displayable);
			}
		}
		return true;
	}
}
