package cm.aptoide.pt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by diogoloureiro on 13/02/2017.
 *
 * Annotation to mark methods and classes used by Partners.
 * Methods and classes marked by this annotation shouldn't be removed or changed to private,
 * without an alternative solution.
 * Please inform the Mobile Vertical Team if you plan to do heavy modifications in this methods.
 */

@Retention(RetentionPolicy.SOURCE)
public @interface Partners {
  // other necessary annotation properties go here
}