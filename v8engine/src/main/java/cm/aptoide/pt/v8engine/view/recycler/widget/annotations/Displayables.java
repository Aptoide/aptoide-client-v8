package cm.aptoide.pt.v8engine.view.recycler.widget.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cm.aptoide.pt.v8engine.view.recycler.widget.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

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
