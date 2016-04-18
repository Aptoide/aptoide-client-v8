/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 18/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import android.view.View;

import cm.aptoide.pt.v8engine.implementationsToRemove.DisplayableImp;
import cm.aptoide.pt.v8engine.implementationsToRemove.DisplayableImp2;
import cm.aptoide.pt.v8engine.implementationsToRemove.WidgetImpl;
import cm.aptoide.pt.v8engine.view.recycler.widget.displayables.EmptyDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.widgets.EmptyWidget;

/**
 * Class that maps each Widget to it's identifier Enum. Useful for mapping purposes, which allows us to have a simpler Adapter basically.
 */
public enum WidgetEnum {
	EMPTY(EmptyWidget.class, EmptyDisplayable.class),
	WIDGET_IMPL(WidgetImpl.class, DisplayableImp.class),
	WIDGET_2(WidgetImpl.class, DisplayableImp2.class),;

	private final Class<? extends Widget> widgetClass;
	private final Class<? extends Displayable> displayableClass;
	private final Displayable displayable;

	WidgetEnum(Class<? extends Widget> widgetClass, Class<? extends Displayable> displayableClass) {
		this.widgetClass = widgetClass;
		this.displayableClass = displayableClass;
		displayable = newDisplayable();
	}

	public Widget newWidget(View view) {

		Class[] cArg = new Class[1];
		cArg[0] = View.class;

		try {
			return widgetClass.getDeclaredConstructor(cArg).newInstance(view);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating widget!");
		}
	}

	public Displayable newDisplayable() {
		try {
			return displayableClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating Displayable!");
		}
	}

	public int getDefaultPerLineCount() {
		return displayable.getDefaultPerLineCount();
	}

	public int getPerLineCount() {
		return displayable.getPerLineCount();
	}
}
