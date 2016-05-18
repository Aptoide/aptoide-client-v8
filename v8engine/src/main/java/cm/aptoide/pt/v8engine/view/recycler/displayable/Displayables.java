/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by sithengineer on 16/05/16.
 */
public class Displayables extends LinkedList<Displayable> {

	@Override
	public void add(int location, Displayable object) {
		if (object instanceof DisplayableGroup) {
			addAll(location, ((DisplayableGroup) object).getChildren());
		} else {
			super.add(location, object);
		}
	}

	@Override
	public boolean add(Displayable object) {
		if (object instanceof DisplayableGroup) {
			return addAll(((DisplayableGroup) object).getChildren());
		} else {
			return super.add(object);
		}
	}

	@Override
	public boolean addAll(int location, Collection<? extends Displayable> collection) {
		for (Displayable displayable : collection) {
			add(location, displayable);
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Displayable> collection) {
		for (Displayable displayable : collection) {
			add(displayable);
		}
		return true;
	}

	@Override
	public void addFirst(Displayable object) {
		if (object instanceof DisplayableGroup) {
			for (Displayable displayable : ((DisplayableGroup) object).getChildren()) {
				addFirst(displayable);
			}
		} else {
			super.addFirst(object);
		}
	}

	@Override
	public void addLast(Displayable object) {
		if (object instanceof DisplayableGroup) {
			for (Displayable displayable : ((DisplayableGroup) object).getChildren()) {
				addLast(displayable);
			}
		} else {
			super.addLast(object);
		}
	}
}
