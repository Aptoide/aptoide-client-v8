/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 28/04/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Annotation used to apply in {@link Widget} classes
 *
 * @author SithEngineer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Displayables {

	Class<? extends Displayable>[] value();
}
