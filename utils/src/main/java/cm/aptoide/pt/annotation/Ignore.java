/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/05/2016.
 */

package cm.aptoide.pt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by sithengineer on 04/05/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {

}
